package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.base.BaseCamelRouteUnitTestSuite;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.camel.route.MarkCampaignAsBotErrorRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
import com.rtt.collector.collectorpoc.exception.RTTCampaignNotFoundException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Random;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MarkCampaignAsBotErrorRouteTest extends BaseCamelRouteUnitTestSuite<MarkCampaignAsBotErrorRoute> {

    @EndpointInject("mock:direct:notifyCampaignMarkedAsBotError")
    protected MockEndpoint notifyCampaignMarkedAsBotErrorEndPoint;

    @EndpointInject("mock:direct:rttCampaignNotFoundHandler")
    protected MockEndpoint rttCampaignNotFoundHandlerEndPoint;

    @Produce("direct:markCampaignAsBotError")
    protected ProducerTemplate markCampaignAsBotErrorEndpoint;

    @InjectMocks
    private MarkCampaignAsBotErrorRoute markCampaignAsBotErrorRoute;

    @Mock
    private UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    @Override
    protected MarkCampaignAsBotErrorRoute getRoute() {
        return markCampaignAsBotErrorRoute;
    }

    @Override
    public RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[] {
                new RouteMockEndpoints(
                        MarkCampaignAsBotErrorRoute.ROUTE_ID,
                        "direct:notifyCampaignMarkedAsBotError"
                ),
                new RouteMockEndpoints(
                        BaseRoute.ROUTE_ID,
                        "direct:rttCampaignNotFoundHandler"
                )
        };
    }

    @Test
    void testAgainstSuccess() throws Exception {
        // Given
        long campaignId = new Random().nextInt();
        RTToolCampaign campaign = new RTToolCampaign() {{
            setId(campaignId);
            setStatus(Status.ACTIVE);
        }};
        RTToolCampaign campaignAfterUpdate = new RTToolCampaign() {{
            setId(campaignId);
            setStatus(Status.BOT_ERROR);
        }};

        // When
        when(updateCampaignStatusUseCase.execute(any())).thenReturn(campaignAfterUpdate);
        markCampaignAsBotErrorEndpoint.sendBodyAndHeader(campaign, KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        // Then
        notifyCampaignMarkedAsBotErrorEndPoint.expectedBodiesReceived(campaignAfterUpdate);
        notifyCampaignMarkedAsBotErrorEndPoint.expectedMessageCount(1);
        notifyCampaignMarkedAsBotErrorEndPoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(0);

        assertMockEndpointsSatisfied();
    }

    @Test
    void testAgainstFailure_CampaignNotFound() throws Exception {
        // Given
        long rttoolCampaignId = new Random().nextInt();

        // When
        when(updateCampaignStatusUseCase.execute(any()))
                .thenThrow(new RTTCampaignNotFoundException(rttoolCampaignId));
        markCampaignAsBotErrorEndpoint.sendBodyAndHeader(true, KEY_RTTOOL_CAMPAIGN_ID, rttoolCampaignId);

        // Then
        notifyCampaignMarkedAsBotErrorEndPoint.expectedMessageCount(0);
        rttCampaignNotFoundHandlerEndPoint.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }
}
