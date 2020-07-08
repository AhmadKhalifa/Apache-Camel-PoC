package com.rtt.collector.collectorpoc.camel.schuedler;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetCampaignsByStatusUseCase;
import org.apache.camel.builder.RouteBuilder;

@Route
public class CollectSchedulerRoute extends RouteBuilder {

    private final GetCampaignsByStatusUseCase getCampaignsByStatusUseCase;

    public CollectSchedulerRoute(GetCampaignsByStatusUseCase getCampaignsByStatusUseCase) {
        this.getCampaignsByStatusUseCase = getCampaignsByStatusUseCase;
    }

    @Override
    public void configure() {
        from("quartz://CollectorSchedulerRoute?cron={{scheduler-routes.collector.cron-expression}}")
                .log("Route CollectorSchedulerRoute: starting new cycle")
                .process(exchange -> exchange.getIn().setBody(getCampaignsByStatusUseCase.execute(
                        GetCampaignsByStatusUseCase.Parameters.build(RTToolCampaign.Status.ACTIVE)
                )))
                .split(body())
                .to("seda:collectRTToolCampaigns");
    }
}
