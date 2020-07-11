package com.rtt.collector.collectorpoc.integration.routes;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.rtt.collector.collectorpoc.base.BaseCamelRouteIntegrationTestSuite;
import com.rtt.collector.collectorpoc.camel.route.MarkCampaignAsBotErrorRoute;
import com.rtt.collector.collectorpoc.camel.route.TriggerBotHubCampaignRoute;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.client.bothub.BotHubClient;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_SCHEDULER_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TriggerCycleIntegrationTest extends BaseCamelRouteIntegrationTestSuite {

    private static final int MSISDNS_COUNT_PER_CAMPAIGN = 10;

    @Value("${rtt.trigger.chunk-size}")
    private int chunkSize;

    @EndpointInject("mock:direct:notifyBotHubCampaignTriggered")
    protected MockEndpoint notifyBotHubCampaignTriggeredEndpoint;

    @EndpointInject("mock:direct:notifyCampaignMarkedAsBotError")
    protected MockEndpoint notifyCampaignMarkedAsBotErrorEndpoint;

    @SpyBean
    protected BotHubClient botHubClient;

    @Override
    protected RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[] {
                new RouteMockEndpoints(
                        TriggerBotHubCampaignRoute.ROUTE_ID,
                        "direct:notifyBotHubCampaignTriggered"
                ),
                new RouteMockEndpoints(
                        MarkCampaignAsBotErrorRoute.ROUTE_ID,
                        "direct:notifyCampaignMarkedAsBotError"
                )
        };
    }

    @Test
    @DatabaseSetup(value = "/seed/trigger/one-campaign-one-bot.xml")
    void testAgainstSuccess_OneCampaignAndValidBot() throws Exception {
        // Given
        int campaignsCount = 1;
        int expectedBotHubCampaignsCount =
                ((int) Math.ceil((double) MSISDNS_COUNT_PER_CAMPAIGN / chunkSize)) * campaignsCount;

        // When
        when(botHubClient.validateBot(any())).thenReturn(true);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.NOT_STARTED,
                KEY_SCHEDULER_TYPE, SchedulerType.TRIGGER
        );

        // Then
        notifyBotHubCampaignTriggeredEndpoint.expectedMessageCount(expectedBotHubCampaignsCount);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(0);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/trigger/three-campaigns-two-bots.xml")
    void testAgainstSuccess_OneCampaignAndInvalidBot() throws Exception {
        // Given
        int campaignsCount = 3;

        // When
        when(botHubClient.validateBot(any())).thenReturn(false);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.NOT_STARTED,
                KEY_SCHEDULER_TYPE, SchedulerType.TRIGGER
        );

        // Then
        notifyBotHubCampaignTriggeredEndpoint.expectedMessageCount(0);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(campaignsCount);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/trigger/three-campaigns-two-bots.xml")
    void testAgainstSuccess_ThreeCampaignsAndTwoValidBots() throws Exception {
        // Given
        int campaignsCount = 3;
        int expectedBotHubCampaignsCount =
                ((int) Math.ceil((double) MSISDNS_COUNT_PER_CAMPAIGN / chunkSize)) * campaignsCount;

        // When
        when(botHubClient.validateBot(any())).thenReturn(true);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.NOT_STARTED,
                KEY_SCHEDULER_TYPE, SchedulerType.TRIGGER
        );

        // Then
        notifyBotHubCampaignTriggeredEndpoint.expectedMessageCount(expectedBotHubCampaignsCount);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(0);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/trigger/three-campaigns-two-bots.xml")
    void testAgainstSuccess_ThreeCampaignsAndTwoInValidBots() throws Exception {
        // Given
        int campaignsCount = 3;

        // When
        when(botHubClient.validateBot(any())).thenReturn(false);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.NOT_STARTED,
                KEY_SCHEDULER_TYPE, SchedulerType.TRIGGER
        );

        // Then
        notifyBotHubCampaignTriggeredEndpoint.expectedMessageCount(0);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(campaignsCount);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/trigger/three-campaigns-two-bots.xml")
    void testAgainstSuccess_ThreeCampaignsOneValidBotOneInvalidBot() throws Exception {
        // Given
        String validBotId = "ABC-DEF-GHI";
        String invalidBotId = "XYZ-DEF-GHI";
        int validCampaignsCount = 2;
        int invalidCampaignsCount = 1;
        int expectedBotHubCampaignsCount =
                ((int) Math.ceil((double) MSISDNS_COUNT_PER_CAMPAIGN / chunkSize)) * validCampaignsCount;

        // When
        when(botHubClient.validateBot(validBotId)).thenReturn(true);
        when(botHubClient.validateBot(invalidBotId)).thenReturn(false);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.NOT_STARTED,
                KEY_SCHEDULER_TYPE, SchedulerType.TRIGGER
        );

        // Then
        notifyBotHubCampaignTriggeredEndpoint.expectedMessageCount(expectedBotHubCampaignsCount);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(invalidCampaignsCount);
        assertMockEndpointsSatisfied();
    }
}
