package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.camel.route.GetCampaignsByStatusRoute;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetCampaignsByStatusUseCase;
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

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_SCHEDULER_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetCampaignByStatusRouteTest extends BaseCamelRouteUnitTestSuite<GetCampaignsByStatusRoute> {

    @EndpointInject("seda:launchCampaigns")
    protected SedaEndpoint launchCampaignsSedaEndpoint;

    @EndpointInject("seda:collectRTToolCampaigns")
    protected SedaEndpoint collectRTToolCampaignsSedaEndpoint;

    @Produce("direct:getCampaignsByStatus")
    protected ProducerTemplate getCampaignsByStatusEndpoint;

    @InjectMocks
    private GetCampaignsByStatusRoute getCampaignsByStatusRoute;

    @Mock
    private GetCampaignsByStatusUseCase getCampaignsByStatusUseCase;

    @Override
    protected GetCampaignsByStatusRoute getRoute() {
        return getCampaignsByStatusRoute;
    }

    @Override
    public RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[0];
    }

    @Test
    void testAgainstSuccess_TriggerScenario() throws Exception {
        // Given
        RTToolCampaign.Status campaignStatus = RTToolCampaign.Status.NOT_STARTED;
        long campaignsCount = 1 + new Random().nextInt(10);
        List<RTToolCampaign> campaigns = new ArrayList<RTToolCampaign>() {{
            for (int i = 0; i < campaignsCount; i++) {
                add(new RTToolCampaign());
            }
        }};

        // When
        when(getCampaignsByStatusUseCase.execute(any())).thenReturn(campaigns);
        getCampaignsByStatusEndpoint.sendBodyAndHeader(campaignStatus, KEY_SCHEDULER_TYPE, SchedulerType.TRIGGER);

        // Then
        assertEquals(campaigns.size(), launchCampaignsSedaEndpoint.getCurrentQueueSize());
        assertEquals(0, collectRTToolCampaignsSedaEndpoint.getCurrentQueueSize());

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstSuccess_CollectorScenario() throws Exception {
        // Given
        RTToolCampaign.Status campaignStatus = RTToolCampaign.Status.ACTIVE;
        long campaignsCount = 1 + new Random().nextInt(10);
        List<RTToolCampaign> campaigns = new ArrayList<RTToolCampaign>() {{
            for (int i = 0; i < campaignsCount; i++) {
                add(new RTToolCampaign());
            }
        }};

        // When
        when(getCampaignsByStatusUseCase.execute(any())).thenReturn(campaigns);
        getCampaignsByStatusEndpoint.sendBodyAndHeader(campaignStatus, KEY_SCHEDULER_TYPE, SchedulerType.COLLECTOR);

        // Then
        assertEquals(campaigns.size(), collectRTToolCampaignsSedaEndpoint.getCurrentQueueSize());
        assertEquals(0, launchCampaignsSedaEndpoint.getCurrentQueueSize());

        assertMockEndpointsSatisfied();
    }
}
