package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.camel.route.TriggerBotHubCampaignRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.service.BotHubCampaignService;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.TriggerBotHubCampaignUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TriggerBotHubCampaignRouteTest extends CamelTestSupport {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:notifyBotHubCampaignTriggered")
    protected MockEndpoint notifyBotHubCampaignTriggeredEndPoint;

    @Produce("seda:triggerCampaign")
    protected ProducerTemplate triggerBotHubCampaignEndpoint;

    @InjectMocks
    private TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase;

    @Mock
    private BotHubCampaignService botHubCampaignService;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.bothub-trigger.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new TriggerBotHubCampaignRoute(triggerBotHubCampaignUseCase);
    }

    @BeforeEach
    void mockAllEndPoints() throws Exception {
        notifyBotHubCampaignTriggeredEndPoint.reset();
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:notifyBotHubCampaignTriggered");
            }
        });
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        BotHubCampaign botHubCampaign = new BotHubCampaign();

        // When
        when(botHubCampaignService.triggerCampaign(any())).thenReturn(botHubCampaign);
        triggerBotHubCampaignEndpoint.sendBody(botHubCampaign);

        // Then
        notifyBotHubCampaignTriggeredEndPoint.expectedBodiesReceived(botHubCampaign);
        notifyBotHubCampaignTriggeredEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}
