package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.camel.route.ChunkCampaignRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.ChunkCampaignUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
import com.rtt.collector.collectorpoc.exception.RTTCampaignNotFoundException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.seda.SedaEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ChunkCampaignRouteTest extends BaseCamelRouteUnitTestSuite<ChunkCampaignRoute> {

    @EndpointInject("seda:triggerCampaign")
    protected SedaEndpoint triggerCampaignSedaEndpoint;

    @EndpointInject("mock:direct:rttCampaignNotFoundHandler")
    protected MockEndpoint rttCampaignNotFoundHandlerEndPoint;

    @Produce("direct:chunkCampaign")
    protected ProducerTemplate chunkCampaignEndpoint;

    @InjectMocks
    private ChunkCampaignRoute chunkCampaignRoute;

    @Mock
    private ChunkCampaignUseCase chunkCampaignUseCase;

    @Mock
    private UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    @Override
    protected ChunkCampaignRoute getRoute() {
        return chunkCampaignRoute;
    }

    @Override
    public RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[] {
                new RouteMockEndpoints(
                        BaseRoute.ROUTE_ID,
                        "direct:rttCampaignNotFoundHandler"
                )
        };
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        Random random = new Random();
        long rttoolCampaignId = random.nextInt();
        RTToolCampaign campaignAfterUpdate = new RTToolCampaign() {{
            setId(rttoolCampaignId);
            setStatus(Status.ACTIVE);
        }};
        long chunksCount = 1 + random.nextInt(10);
        List<BotHubCampaign> chunkedBotHubCampaigns = new ArrayList<BotHubCampaign>() {{
            for (int i = 0; i < chunksCount; i++) {
                add(new BotHubCampaign());
            }
        }};

        // When
        when(updateCampaignStatusUseCase.execute(any())).thenReturn(campaignAfterUpdate);
        when(chunkCampaignUseCase.execute(any())).thenReturn(chunkedBotHubCampaigns);
        chunkCampaignEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, rttoolCampaignId);

        // Then
        assertEquals(chunkedBotHubCampaigns.size(), triggerCampaignSedaEndpoint.getCurrentQueueSize());
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstFailure_CampaignNotFoundWhenTryingToUpdateCampaignStatus() throws Exception {
        // Given
        long rttoolCampaignId = new Random().nextInt();

        // When
        when(updateCampaignStatusUseCase.execute(any()))
                .thenThrow(new RTTCampaignNotFoundException(rttoolCampaignId));
        chunkCampaignEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, rttoolCampaignId);

        // Then
        assertEquals(0, triggerCampaignSedaEndpoint.getCurrentQueueSize());
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstFailure_CampaignNotFoundWhenTryingToChunkIt() throws Exception {
        // Given
        Random random = new Random();
        long rttoolCampaignId = random.nextInt();
        RTToolCampaign campaignAfterUpdate = new RTToolCampaign() {{
            setId(rttoolCampaignId);
            setStatus(Status.ACTIVE);
        }};

        // When
        when(updateCampaignStatusUseCase.execute(any())).thenReturn(campaignAfterUpdate);
        when(chunkCampaignUseCase.execute(any()))
                .thenThrow(new RTTCampaignNotFoundException(rttoolCampaignId));
        chunkCampaignEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, rttoolCampaignId);

        // Then
        assertEquals(0, triggerCampaignSedaEndpoint.getCurrentQueueSize());
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}
