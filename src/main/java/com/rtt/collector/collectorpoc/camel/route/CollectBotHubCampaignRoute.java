package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.usecase.CollectBotHubCampaignResultsUseCase;

@Route
public class CollectBotHubCampaignRoute extends BaseRoute {

    public static final String ROUTE_ID = CollectBotHubCampaignRoute.class.getSimpleName();

    private final CollectBotHubCampaignResultsUseCase collectBotHubCampaignResultsUseCase;

    public CollectBotHubCampaignRoute(CollectBotHubCampaignResultsUseCase collectBotHubCampaignResultsUseCase) {
        this.collectBotHubCampaignResultsUseCase = collectBotHubCampaignResultsUseCase;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        from("seda:collectBotHubCampaign?concurrentConsumers={{scheduler-routes.bothub-collector.thread-pool}}")
                .routeId(ROUTE_ID)
                .process(exchange -> exchange.getIn().setBody(collectBotHubCampaignResultsUseCase.execute(
                        CollectBotHubCampaignResultsUseCase.Parameters.build(
                                exchange.getIn().getBody(BotHubCampaign.class).getId()
                        )
                )))
                .to("direct:notifyBotHubCampaignCollected");

        from("direct:notifyBotHubCampaignCollected")
                .log("Campaign collected: ${body}");
    }
}
