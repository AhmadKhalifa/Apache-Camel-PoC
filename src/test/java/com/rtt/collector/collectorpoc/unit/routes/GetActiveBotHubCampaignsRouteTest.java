package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.camel.route.GetActiveBotHubCampaignsRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetActiveBotHubCampaignsUseCase;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GetActiveBotHubCampaignsRouteTest extends CamelTestSupport {

    @EndpointInject("seda:collectBotHubCampaign")
    protected SedaEndpoint collectBotHubCampaignSedaEndpoint;

    @Produce("direct:getActiveBotHubCampaigns")
    protected ProducerTemplate getActiveBotHubCampaignsEndpoint;

    @InjectMocks
    private GetActiveBotHubCampaignsRoute getActiveBotHubCampaignsRoute;

    @Mock
    private GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return getActiveBotHubCampaignsRoute;
    }

    @Test
    void testAgainstSuccess() throws Exception {
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

        assertMockEndpointsSatisfied();
    }
}
