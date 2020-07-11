package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.camel.route.TriggerBotHubCampaignRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.TriggerBotHubCampaignUseCase;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TriggerBotHubCampaignRouteTest extends BaseCamelRouteUnitTestSuite<TriggerBotHubCampaignRoute> {

    private static final int THREAD_POOL = 1;

    @EndpointInject("mock:direct:notifyBotHubCampaignTriggered")
    protected MockEndpoint notifyBotHubCampaignTriggeredEndPoint;

    @Produce("seda:triggerCampaign")
    protected ProducerTemplate triggerBotHubCampaignEndpoint;

    @InjectMocks
    private TriggerBotHubCampaignRoute triggerBotHubCampaignRoute;

    @Mock
    private TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase;

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        return new Properties() {{
            put("scheduler-routes.bothub-trigger.thread-pool", THREAD_POOL);
        }};
    }

    @Override
    protected TriggerBotHubCampaignRoute getRoute() {
        return triggerBotHubCampaignRoute;
    }

    @Override
    protected String[] getEndpointsToMock() {
        return new String[]{"direct:notifyBotHubCampaignTriggered"};
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        BotHubCampaign botHubCampaign = new BotHubCampaign();

        // When
        when(triggerBotHubCampaignUseCase.execute(any())).thenReturn(botHubCampaign);
        triggerBotHubCampaignEndpoint.sendBody(botHubCampaign);

        // Then
        notifyBotHubCampaignTriggeredEndPoint.expectedBodiesReceived(botHubCampaign);
        notifyBotHubCampaignTriggeredEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}
