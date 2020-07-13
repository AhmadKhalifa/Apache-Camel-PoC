package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.camel.route.GetActiveBotHubCampaignsRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetActiveBotHubCampaignsUseCase;
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

public class GetActiveBotHubCampaignsRouteTest extends BaseCamelRouteUnitTestSuite<GetActiveBotHubCampaignsRoute> {

    @EndpointInject("seda:collectBotHubCampaign")
    protected SedaEndpoint collectBotHubCampaignSedaEndpoint;

    @Produce("direct:getActiveBotHubCampaigns")
    protected ProducerTemplate getActiveBotHubCampaignsEndpoint;

    @EndpointInject("mock:direct:rttCampaignNotFoundHandler")
    protected MockEndpoint rttCampaignNotFoundHandlerEndPoint;

    @InjectMocks
    private GetActiveBotHubCampaignsRoute getActiveBotHubCampaignsRoute;

    @Mock
    private GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase;

    @Override
    protected GetActiveBotHubCampaignsRoute getRoute() {
        return getActiveBotHubCampaignsRoute;
    }

    @Override
    public RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[]{
                new RouteMockEndpoints(
                        BaseRoute.ROUTE_ID,
                        "direct:rttCampaignNotFoundHandler"
                )
        };
    }

    @Test
    void testAgainstSuccess() {
        // Given
        Random random = new Random();
        long rttoolCampaignId = random.nextInt();
        long chunksCount = 1 + random.nextInt(10);
        List<BotHubCampaign> chunkedBotHubCampaigns = new ArrayList<BotHubCampaign>() {{
            for (int i = 0; i < chunksCount; i++) {
                add(new BotHubCampaign());
            }
        }};

        // When
        when(getActiveBotHubCampaignsUseCase.execute(any())).thenReturn(chunkedBotHubCampaigns);
        getActiveBotHubCampaignsEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, rttoolCampaignId);

        // Then
        assertEquals(chunkedBotHubCampaigns.size(), collectBotHubCampaignSedaEndpoint.getCurrentQueueSize());
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(0);
    }

    @Test
    void testAgainstSuccess_CampaignNotFound() {
        // Given
        long rttoolCampaignId = new Random().nextInt();

        // When
        when(getActiveBotHubCampaignsUseCase.execute(any()))
                .thenThrow(new RTTCampaignNotFoundException(rttoolCampaignId));
        getActiveBotHubCampaignsEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, rttoolCampaignId);

        // Then
        assertEquals(0, collectBotHubCampaignSedaEndpoint.getCurrentQueueSize());
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(1);
    }
}
