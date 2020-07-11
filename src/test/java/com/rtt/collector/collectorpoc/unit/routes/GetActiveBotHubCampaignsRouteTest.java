package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.camel.route.GetActiveBotHubCampaignsRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetActiveBotHubCampaignsUseCase;
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

public class GetActiveBotHubCampaignsRouteTest extends BaseCamelRouteUnitTestSuite<GetActiveBotHubCampaignsRoute> {

    @EndpointInject("seda:collectBotHubCampaign")
    protected SedaEndpoint collectBotHubCampaignSedaEndpoint;

    @Produce("direct:getActiveBotHubCampaigns")
    protected ProducerTemplate getActiveBotHubCampaignsEndpoint;

    @InjectMocks
    private GetActiveBotHubCampaignsRoute getActiveBotHubCampaignsRoute;

    @Mock
    private GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase;

    @Override
    protected GetActiveBotHubCampaignsRoute getRoute() {
        return getActiveBotHubCampaignsRoute;
    }

    @Override
    protected String[] getEndpointsToMock() {
        return new String[0];
    }

    @Test
    void testAgainstSuccess() {
        // Given
        Random random = new Random();
        long campaignId = random.nextInt();
        long chunksCount = 1 + random.nextInt(10);
        List<BotHubCampaign> chunkedBotHubCampaigns = new ArrayList<BotHubCampaign>() {{
            for (int i = 0; i < chunksCount; i++) {
                add(new BotHubCampaign());
            }
        }};

        // When
        when(getActiveBotHubCampaignsUseCase.execute(any())).thenReturn(chunkedBotHubCampaigns);
        getActiveBotHubCampaignsEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        // Then
        assertEquals(chunkedBotHubCampaigns.size(), collectBotHubCampaignSedaEndpoint.getCurrentQueueSize());
    }
}
