package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.TriggerBotHubCampaignUseCase;
import org.apache.camel.builder.RouteBuilder;

@Route
public class TriggerBotHubCampaignRoute extends RouteBuilder {

    private final TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase;

    public TriggerBotHubCampaignRoute(TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase) {
        this.triggerBotHubCampaignUseCase = triggerBotHubCampaignUseCase;
    }

    @Override
    public void configure() {
        from("seda:triggerCampaign?concurrentConsumers={{scheduler-routes.bothub-trigger.thread-pool}}")
                .process(exchange -> exchange.getIn().setBody(triggerBotHubCampaignUseCase.execute(
                        TriggerBotHubCampaignUseCase.Parameters.build(exchange.getIn().getBody(BotHubCampaign.class))
                )))
                .to("direct:notifyBotHubCampaignTriggered");

        from("direct:notifyBotHubCampaignTriggered")
                .log("{body}");
    }
}
