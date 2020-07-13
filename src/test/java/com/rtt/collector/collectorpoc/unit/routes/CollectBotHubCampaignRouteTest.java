package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.camel.route.CollectBotHubCampaignRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.CollectBotHubCampaignResultsUseCase;
import com.rtt.collector.collectorpoc.exception.BotHubCampaignNotFoundException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Properties;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CollectBotHubCampaignRouteTest extends BaseCamelRouteUnitTestSuite<CollectBotHubCampaignRoute> {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:notifyBotHubCampaignCollected")
    protected MockEndpoint notifyBotHubCampaignCollectedEndPoint;

    @EndpointInject("mock:direct:botHubCampaignNotFoundHandler")
    protected MockEndpoint botHubCampaignNotFoundHandlerEndPoint;

    @Produce("seda:collectBotHubCampaign")
    protected ProducerTemplate collectBotHubCampaignEndpoint;

    @InjectMocks
    private CollectBotHubCampaignRoute collectBotHubCampaignRoute;

    @Mock
    private CollectBotHubCampaignResultsUseCase collectBotHubCampaignResultsUseCase;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.bothub-collector.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected CollectBotHubCampaignRoute getRoute() {
        return collectBotHubCampaignRoute;
    }

    @Override
    public RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[] {
                new RouteMockEndpoints(
                        CollectBotHubCampaignRoute.ROUTE_ID,
                        "direct:notifyBotHubCampaignCollected"
                ),
                new RouteMockEndpoints(
                        BaseRoute.ROUTE_ID,
                        "direct:botHubCampaignNotFoundHandler"
                )
        };
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        long botHubCampaignId = new Random().nextInt();
        BotHubCampaign botHubCampaign = new BotHubCampaign() {{
            setId(botHubCampaignId);
        }};

        // When
        when(collectBotHubCampaignResultsUseCase.execute(any())).thenReturn(botHubCampaign);
        collectBotHubCampaignEndpoint.sendBody(botHubCampaign);

        // Then
        notifyBotHubCampaignCollectedEndPoint.expectedBodiesReceived(botHubCampaign);
        notifyBotHubCampaignCollectedEndPoint.expectedMessageCount(1);
        botHubCampaignNotFoundHandlerEndPoint.expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstFailure_BotHubCampaignNotFound() throws Exception {
        // Given
        long botHubCampaignId = new Random().nextInt();
        BotHubCampaign botHubCampaign = new BotHubCampaign() {{
            setId(botHubCampaignId);
        }};

        // When
        when(collectBotHubCampaignResultsUseCase.execute(any()))
                .thenThrow(new BotHubCampaignNotFoundException(botHubCampaignId));
        collectBotHubCampaignEndpoint.sendBody(botHubCampaign);

        // Then
        notifyBotHubCampaignCollectedEndPoint.expectedMessageCount(0);
        botHubCampaignNotFoundHandlerEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}