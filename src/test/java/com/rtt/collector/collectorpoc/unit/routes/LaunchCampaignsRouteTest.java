package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
import com.rtt.collector.collectorpoc.camel.route.LaunchCampaignsRoute;
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

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LaunchCampaignsRouteTest extends BaseCamelRouteUnitTestSuite<LaunchCampaignsRoute> {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:chunkCampaign")
    protected MockEndpoint chuckCampaignEndpoint;

    @EndpointInject("mock:direct:markCampaignAsBotError")
    protected MockEndpoint markCampaignAsBotErrorEndpoint;

    @Produce("seda:launchCampaigns")
    protected ProducerTemplate launchCampaignsEndpoint;

    @InjectMocks
    private LaunchCampaignsRoute launchCampaignsRoute;

    @Mock
    private ValidateBotUseCase validateBotUseCase;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.trigger.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected LaunchCampaignsRoute getRoute() {
        return launchCampaignsRoute;
    }

    @Override
    protected String[] getEndpointsToMock() {
        return new String[]{"direct:chunkCampaign", "direct:markCampaignAsBotError"};
    }

    @Test
    void testAgainstSuccess_ValidBot() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
        }};

        // When
        when(validateBotUseCase.execute(any())).thenReturn(true);
        launchCampaignsEndpoint.sendBody(campaign);

        // Then
        chuckCampaignEndpoint.expectedBodiesReceived(true);
        chuckCampaignEndpoint.expectedMessageCount(1);
        chuckCampaignEndpoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        markCampaignAsBotErrorEndpoint.expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstSuccess_InvalidBot() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
        }};

        // When
        when(validateBotUseCase.execute(any())).thenReturn(false);
        launchCampaignsEndpoint.sendBody(campaign);

        // Then
        chuckCampaignEndpoint.expectedMessageCount(0);

        markCampaignAsBotErrorEndpoint.expectedBodiesReceived(false);
        markCampaignAsBotErrorEndpoint.expectedMessageCount(1);
        markCampaignAsBotErrorEndpoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        assertMockEndpointsSatisfied();
    }
}
