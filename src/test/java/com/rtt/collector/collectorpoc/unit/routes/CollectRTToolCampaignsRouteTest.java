package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.bot.model.Bot;
import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
import com.rtt.collector.collectorpoc.camel.route.CollectRTToolCampaignsRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KET_BOT_HUB_BOT_ID;
import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CollectRTToolCampaignsRouteTest extends BaseCamelRouteUnitTestSuite<CollectRTToolCampaignsRoute> {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:getActiveBotHubCampaigns")
    protected MockEndpoint getActiveBotHubCampaignsEndpoint;

    @EndpointInject("mock:direct:markCampaignAsBotError")
    protected MockEndpoint markCampaignAsBotErrorEndpoint;

    @Produce("seda:collectRTToolCampaigns")
    protected ProducerTemplate collectRTToolCampaignsEndpoint;

    @InjectMocks
    private CollectRTToolCampaignsRoute collectRTToolCampaignsRoute;

    @Mock
    private ValidateBotUseCase validateBotUseCase;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.collector.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected CollectRTToolCampaignsRoute getRoute() {
        return collectRTToolCampaignsRoute;
    }

    @Override
    protected String[] getEndpointsToMock() {
        return new String[]{"direct:getActiveBotHubCampaigns", "direct:markCampaignAsBotError"};
    }

    @Test
    void testAgainstSuccess_ValidBot() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        String botHubBotId = UUID.randomUUID().toString();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
            setBot(new Bot() {{
                setBotHubId(botHubBotId);
            }});
        }};

        // When
        when(validateBotUseCase.execute(any())).thenReturn(true);
        collectRTToolCampaignsEndpoint.sendBody(campaign);

        // Then
        getActiveBotHubCampaignsEndpoint.expectedBodiesReceived(true);
        getActiveBotHubCampaignsEndpoint.expectedMessageCount(1);
        getActiveBotHubCampaignsEndpoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);
        getActiveBotHubCampaignsEndpoint.expectedHeaderReceived(KET_BOT_HUB_BOT_ID, botHubBotId);

        markCampaignAsBotErrorEndpoint.expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstSuccess_InvalidBot() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        String botHubBotId = UUID.randomUUID().toString();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
            setBot(new Bot() {{
                setBotHubId(botHubBotId);
            }});
        }};

        // When
        when(validateBotUseCase.execute(any())).thenReturn(false);
        collectRTToolCampaignsEndpoint.sendBody(campaign);

        // Then
        getActiveBotHubCampaignsEndpoint.expectedMessageCount(0);

        markCampaignAsBotErrorEndpoint.expectedBodiesReceived(false);
        markCampaignAsBotErrorEndpoint.expectedMessageCount(1);
        markCampaignAsBotErrorEndpoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);
        markCampaignAsBotErrorEndpoint.expectedHeaderReceived(KET_BOT_HUB_BOT_ID, botHubBotId);

        assertMockEndpointsSatisfied();
    }
}