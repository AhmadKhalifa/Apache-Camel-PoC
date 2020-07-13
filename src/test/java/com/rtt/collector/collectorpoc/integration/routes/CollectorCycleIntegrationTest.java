package com.rtt.collector.collectorpoc.integration.routes;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.rtt.collector.collectorpoc.base.BaseCamelRouteIntegrationTestSuite;
import com.rtt.collector.collectorpoc.camel.route.CollectBotHubCampaignRoute;
import com.rtt.collector.collectorpoc.camel.route.MarkCampaignAsBotErrorRoute;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import com.rtt.collector.collectorpoc.campaign.combo.data.BotHubCampaignRepository;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignMapper;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.client.bothub.ComboClient;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_SCHEDULER_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CollectorCycleIntegrationTest extends BaseCamelRouteIntegrationTestSuite {

    private static final int BOT_HUB_CAMPAIGNS_COUNT_PER_RTT_CAMPAIGN = 3;

    @EndpointInject("mock:direct:notifyBotHubCampaignCollected")
    protected MockEndpoint notifyBotHubCampaignCollectedEndpoint;

    @EndpointInject("mock:direct:notifyCampaignMarkedAsBotError")
    protected MockEndpoint notifyCampaignMarkedAsBotErrorEndpoint;

    @Autowired
    private BotHubCampaignRepository botHubCampaignRepository;

    @Autowired
    private BotHubCampaignMapper botHubCampaignMapper;

    @SpyBean
    protected ComboClient botHubClient;

    @Override
    public RouteMockEndpoints[] getEndpointsToMock() {
        return new RouteMockEndpoints[] {
                new RouteMockEndpoints(
                        CollectBotHubCampaignRoute.ROUTE_ID,
                        "direct:notifyBotHubCampaignCollected"
                ),
                new RouteMockEndpoints(
                        MarkCampaignAsBotErrorRoute.ROUTE_ID,
                        "direct:notifyCampaignMarkedAsBotError"
                )
        };
    }

    @BeforeEach
    protected void initClient() {
        botHubCampaignRepository.findAll().forEach(botHubCampaignEntity ->
                botHubClient.triggeredCampaigns.add(botHubCampaignMapper.toDto(botHubCampaignEntity, false))
        );
    }

    @Test
    @DatabaseSetup(value = "/seed/collector/one-campaign-one-bot.xml")
    void testAgainstSuccess_OneCampaignAndValidBot() throws Exception {
        // Given
        int campaignsCount = 1;
        int expectedBotHubCampaignsCount = BOT_HUB_CAMPAIGNS_COUNT_PER_RTT_CAMPAIGN * campaignsCount;

        // When
        when(botHubClient.validateBot(any())).thenReturn(true);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.ACTIVE,
                KEY_SCHEDULER_TYPE, SchedulerType.COLLECTOR
        );

        // Then
        notifyBotHubCampaignCollectedEndpoint.expectedMessageCount(expectedBotHubCampaignsCount);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(0);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/collector/three-campaigns-two-bots.xml")
    void testAgainstSuccess_OneCampaignAndInvalidBot() throws Exception {
        // Given
        int campaignsCount = 3;

        // When
        when(botHubClient.validateBot(any())).thenReturn(false);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.ACTIVE,
                KEY_SCHEDULER_TYPE, SchedulerType.COLLECTOR
        );

        // Then
        notifyBotHubCampaignCollectedEndpoint.expectedMessageCount(0);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(campaignsCount);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/collector/three-campaigns-two-bots.xml")
    void testAgainstSuccess_ThreeCampaignsAndTwoValidBots() throws Exception {
        // Given
        int campaignsCount = 3;
        int expectedBotHubCampaignsCount = BOT_HUB_CAMPAIGNS_COUNT_PER_RTT_CAMPAIGN * campaignsCount;

        // When
        when(botHubClient.validateBot(any())).thenReturn(true);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.ACTIVE,
                KEY_SCHEDULER_TYPE, SchedulerType.COLLECTOR
        );

        // Then
        notifyBotHubCampaignCollectedEndpoint.expectedMessageCount(expectedBotHubCampaignsCount);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(0);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/collector/three-campaigns-two-bots.xml")
    void testAgainstSuccess_ThreeCampaignsAndTwoInValidBots() throws Exception {
        // Given
        int campaignsCount = 3;

        // When
        when(botHubClient.validateBot(any())).thenReturn(false);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.ACTIVE,
                KEY_SCHEDULER_TYPE, SchedulerType.COLLECTOR
        );

        // Then
        notifyBotHubCampaignCollectedEndpoint.expectedMessageCount(0);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(campaignsCount);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DatabaseSetup(value = "/seed/collector/three-campaigns-two-bots.xml")
    void testAgainstSuccess_ThreeCampaignsOneValidBotOneInvalidBot() throws Exception {
        // Given
        String validBotId = "ABC-DEF-GHI";
        String invalidBotId = "XYZ-DEF-GHI";
        int validCampaignsCount = 2;
        int invalidCampaignsCount = 1;
        int expectedBotHubCampaignsCount = BOT_HUB_CAMPAIGNS_COUNT_PER_RTT_CAMPAIGN * validCampaignsCount;

        // When
        when(botHubClient.validateBot(validBotId)).thenReturn(true);
        when(botHubClient.validateBot(invalidBotId)).thenReturn(false);
        producerTemplate.sendBodyAndHeader(
                "direct:getCampaignsByStatus",
                RTToolCampaign.Status.ACTIVE,
                KEY_SCHEDULER_TYPE, SchedulerType.COLLECTOR
        );

        // Then
        notifyBotHubCampaignCollectedEndpoint.expectedMessageCount(expectedBotHubCampaignsCount);
        notifyCampaignMarkedAsBotErrorEndpoint.expectedMessageCount(invalidCampaignsCount);
        assertMockEndpointsSatisfied();
    }
}
