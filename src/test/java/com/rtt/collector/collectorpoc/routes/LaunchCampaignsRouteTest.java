package com.rtt.collector.collectorpoc.routes;

import com.rtt.collector.collectorpoc.bot.service.ComboBotService;
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
    protected MockEndpoint chuckCampaignEndPoint;

    @EndpointInject("mock:direct:markCampaignAsBotError")
    protected MockEndpoint markCampaignAsBotErrorEndPoint;

    @Produce("seda:launchCampaigns")
    protected ProducerTemplate template;

    @InjectMocks
    private ValidateBotUseCase validateBotUseCase;

    @Mock
    private ComboBotService botService;

    @BeforeEach
    void mockAllEndPoints() throws Exception {
        chuckCampaignEndPoint.reset();
        markCampaignAsBotErrorEndPoint.reset();
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:chunkCampaign", "direct:markCampaignAsBotError");
            }
        });
    }

    @Test
    void testWhenBotIsValid() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
        }};

        // When
        when(botService.validateBot(any())).thenReturn(true);
        template.sendBody(campaign);

        // Then
        chuckCampaignEndPoint.expectedBodiesReceived(true);
        chuckCampaignEndPoint.expectedMessageCount(1);
        chuckCampaignEndPoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        markCampaignAsBotErrorEndPoint.expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testWhenBotIsNotValid() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
        }};

        // When
        when(botService.validateBot(any())).thenReturn(false);
        template.sendBody(campaign);

        // Then
        chuckCampaignEndPoint.expectedMessageCount(0);

        markCampaignAsBotErrorEndPoint.expectedBodiesReceived(false);
        markCampaignAsBotErrorEndPoint.expectedMessageCount(1);
        markCampaignAsBotErrorEndPoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        Properties extra = new Properties();
        extra.put("scheduler-routes.trigger.thread-pool", THREAD_POOL);
        return extra;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new LaunchCampaignsRoute(validateBotUseCase);
    }
}
