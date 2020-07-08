package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.CollectBotHubCampaignResultsUseCase;
import org.apache.camel.builder.RouteBuilder;

@Route
public class CollectBotHubCampaignRoute extends RouteBuilder {

    private final CollectBotHubCampaignResultsUseCase collectBotHubCampaignResultsUseCase;

    public CollectBotHubCampaignRoute(CollectBotHubCampaignResultsUseCase collectBotHubCampaignResultsUseCase) {
        this.collectBotHubCampaignResultsUseCase = collectBotHubCampaignResultsUseCase;
    }

    @Override
    public void configure() {
        from("seda:collectBotHubCampaign?concurrentConsumers={{scheduler-routes.bothub-collector.thread-pool}}")
                .process(exchange -> collectBotHubCampaignResultsUseCase.execute(
                        CollectBotHubCampaignResultsUseCase.Parameters.build(
                                exchange.getIn().getBody(BotHubCampaign.class).getId()
                        )
                ));
    }
}
