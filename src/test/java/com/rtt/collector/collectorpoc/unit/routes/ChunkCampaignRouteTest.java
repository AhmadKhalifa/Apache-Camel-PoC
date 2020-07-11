package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.camel.route.ChunkCampaignRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.ChunkCampaignUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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
    protected String[] getEndpointsToMock() {
        return new String[]{"direct:chunkCampaignError"};
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        Random random = new Random();
        long campaignId = random.nextInt();
        RTToolCampaign campaignAfterUpdate = new RTToolCampaign() {{
            setId(campaignId);
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
        chunkCampaignEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        // Then
        assertEquals(chunkedBotHubCampaigns.size(), triggerCampaignSedaEndpoint.getCurrentQueueSize());

        assertMockEndpointsSatisfied();
    }
}
