package com.rtt.collector.collectorpoc.camel.schuedler;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetCampaignsByStatusUseCase;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

@Route
public class CollectSchedulerRoute extends RouteBuilder {

    @Value("${scheduler-routes.collector.cron-expression}")
    private String cronExpression;

    private final GetCampaignsByStatusUseCase getCampaignsByStatusUseCase;

    public CollectSchedulerRoute(GetCampaignsByStatusUseCase getCampaignsByStatusUseCase) {
        this.getCampaignsByStatusUseCase = getCampaignsByStatusUseCase;
    }

    @Override
    public void configure() {
        from(String.format("quartz://CollectorSchedulerRoute?cron=%s", cronExpression))
                .log("Route CollectorSchedulerRoute: starting new cycle")
                .process(exchange -> exchange.getIn().setBody(getCampaignsByStatusUseCase.execute(
                        GetCampaignsByStatusUseCase.Parameters.build(RTToolCampaign.Status.ACTIVE)
                )))
                .split(body())
                .to("seda:collectRTToolCampaigns");
    }
}
