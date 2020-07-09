package com.rtt.collector.collectorpoc.routes;

import com.rtt.collector.collectorpoc.bot.service.BotService;
import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
import com.rtt.collector.collectorpoc.camel.route.LaunchCampaignsRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Properties;
import java.util.Random;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LaunchCampaignsRouteTest extends CamelTestSupport {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:chunkCampaign")
    protected MockEndpoint chuckCampaignEndpoint;

    @EndpointInject("mock:direct:markCampaignAsBotError")
    protected MockEndpoint markCampaignAsBotErrorEndpoint;

    @Produce("seda:launchCampaigns")
    protected ProducerTemplate launchCampaignsEndpoint;

    @InjectMocks
    private ValidateBotUseCase validateBotUseCase;

    @Mock
    private BotService botService;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.trigger.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new LaunchCampaignsRoute(validateBotUseCase);
    }

    @BeforeEach
    void mockAllEndPoints() throws Exception {
        chuckCampaignEndpoint.reset();
        markCampaignAsBotErrorEndpoint.reset();
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:chunkCampaign", "direct:markCampaignAsBotError");
            }
        });
    }

    @Test
    void testAgainstSuccess_ValidBot() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
        }};

        // When
        when(botService.validateBot(any())).thenReturn(true);
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
        when(botService.validateBot(any())).thenReturn(false);
        launchCampaignsEndpoint.sendBody(campaign);

        // Then
        chuckCampaignEndpoint.expectedMessageCount(0);

        markCampaignAsBotErrorEndpoint.expectedBodiesReceived(false);
        markCampaignAsBotErrorEndpoint.expectedMessageCount(1);
        markCampaignAsBotErrorEndpoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        assertMockEndpointsSatisfied();
    }
}
