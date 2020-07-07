package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.TriggerBotHubCampaignUseCase;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

@Route
public class TriggerBotHubCampaignRoute extends RouteBuilder {


    @Value("${scheduler-routes.bothub-trigger.thread-pool}")
    private int threadPool;

    private final TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase;

    public TriggerBotHubCampaignRoute(TriggerBotHubCampaignUseCase triggerBotHubCampaignUseCase) {
        this.triggerBotHubCampaignUseCase = triggerBotHubCampaignUseCase;
    }

    @Override
    public void configure() {
        from(String.format("seda:triggerCampaign?concurrentConsumers=%d", threadPool))
                .process(exchange -> triggerBotHubCampaignUseCase.execute(TriggerBotHubCampaignUseCase.Parameters.build(
                        exchange.getIn().getBody(BotHubCampaign.class)
                )));
    }
}
