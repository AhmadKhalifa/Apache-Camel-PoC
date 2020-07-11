package com.rtt.collector.collectorpoc.unit.routes;

import com.rtt.collector.collectorpoc.camel.route.MarkCampaignAsBotErrorRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.service.RTToolCampaignService;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
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

import java.util.Random;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MarkCampaignAsBotErrorRouteTest extends CamelTestSupport {

    @EndpointInject("mock:direct:notifyCampaignMarkedAsBotError")
    protected MockEndpoint notifyCampaignMarkedAsBotErrorEndPoint;

    @Produce("direct:markCampaignAsBotError")
    protected ProducerTemplate markCampaignAsBotErrorEndpoint;

    @InjectMocks
    private UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    @Mock
    private RTToolCampaignService rtToolCampaignService;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new MarkCampaignAsBotErrorRoute(updateCampaignStatusUseCase);
    }

    @BeforeEach
    void mockAllEndPoints() throws Exception {
        notifyCampaignMarkedAsBotErrorEndPoint.reset();
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:notifyCampaignMarkedAsBotError");
            }
        });
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
        when(rtToolCampaignService.updateCampaignStatus(anyLong(), any())).thenReturn(campaignAfterUpdate);
        markCampaignAsBotErrorEndpoint.sendBodyAndHeader(campaign, KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        // Then
        notifyCampaignMarkedAsBotErrorEndPoint.expectedBodiesReceived(campaignAfterUpdate);
        notifyCampaignMarkedAsBotErrorEndPoint.expectedMessageCount(1);
        notifyCampaignMarkedAsBotErrorEndPoint.expectedHeaderReceived(KEY_RTTOOL_CAMPAIGN_ID, campaignId);

        assertMockEndpointsSatisfied();
    }
}
