package com.rtt.collector.collectorpoc.camel.schuedler;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetCampaignsByStatusUseCase;
import org.apache.camel.builder.RouteBuilder;

@Route
public class TriggerSchedulerRoute extends RouteBuilder {

    private final GetCampaignsByStatusUseCase getCampaignsByStatusUseCase;

    public TriggerSchedulerRoute(GetCampaignsByStatusUseCase getCampaignsByStatusUseCase) {
        this.getCampaignsByStatusUseCase = getCampaignsByStatusUseCase;
    }

    @Override
    public void configure() {
        from("quartz://TriggerSchedulerRoute?cron={{scheduler-routes.trigger.cron-expression}}")
                .log("Route TriggerSchedulerRoute: starting new cycle")
                .process(exchange -> exchange.getIn().setBody(getCampaignsByStatusUseCase.execute(
                        GetCampaignsByStatusUseCase.Parameters.build(RTToolCampaign.Status.NOT_STARTED)
                )))
                .split(body())
                .to("seda:launchCampaigns");
    }
}
