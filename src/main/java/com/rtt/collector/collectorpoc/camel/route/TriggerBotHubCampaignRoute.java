package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.TriggerBotHubCampaignUseCase;

@Route
public class TriggerBotHubCampaignRoute extends BaseRoute {

    public static final String ROUTE_ID = TriggerBotHubCampaignRoute.class.getSimpleName();

    private final TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase;

    public TriggerBotHubCampaignRoute(TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase) {
        this.triggerBotHubCampaignUseCase = triggerBotHubCampaignUseCase;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        from("seda:triggerCampaign?concurrentConsumers={{scheduler-routes.bothub-trigger.thread-pool}}")
                .routeId(ROUTE_ID)
                .process(exchange -> exchange.getIn().setBody(triggerBotHubCampaignUseCase.execute(
                        TriggerBotHubCampaignUseCase.Parameters.build(exchange.getIn().getBody(BotHubCampaign.class))
                )))
                .to("direct:notifyBotHubCampaignTriggered");

        from("direct:notifyBotHubCampaignTriggered")
                .log("Campaign triggered: ${body}");
    }
}
