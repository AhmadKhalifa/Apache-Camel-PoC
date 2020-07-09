package com.rtt.collector.collectorpoc.routes;

import com.rtt.collector.collectorpoc.camel.route.CollectBotHubCampaignRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.service.BotHubCampaignService;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.CollectBotHubCampaignResultsUseCase;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CollectBotHubCampaignRouteTest extends CamelTestSupport {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:notifyBotHubCampaignCollected")
    protected MockEndpoint notifyBotHubCampaignCollectedEndPoint;

    @Produce("seda:collectBotHubCampaign")
    protected ProducerTemplate collectBotHubCampaignEndpoint;

    @InjectMocks
    private CollectBotHubCampaignResultsUseCase collectBotHubCampaignResultsUseCase;

    @Mock
    private BotHubCampaignService botHubCampaignService;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.bothub-collector.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new CollectBotHubCampaignRoute(collectBotHubCampaignResultsUseCase);
    }

    @BeforeEach
    void mockAllEndPoints() throws Exception {
        notifyBotHubCampaignCollectedEndPoint.reset();
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:notifyBotHubCampaignCollected");
            }
        });
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        long botHubCampaignId = new Random().nextInt();
        BotHubCampaign botHubCampaign = new BotHubCampaign() {{
            setId(botHubCampaignId);
        }};

        // When
        when(botHubCampaignService.collectCampaignResults(anyLong())).thenReturn(botHubCampaign);
        collectBotHubCampaignEndpoint.sendBody(botHubCampaign);

        // Then
        notifyBotHubCampaignCollectedEndPoint.expectedBodiesReceived(botHubCampaign);
        notifyBotHubCampaignCollectedEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}