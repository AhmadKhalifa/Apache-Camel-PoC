package com.rtt.collector.collectorpoc.camel.schuedler;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetCampaignsByStatusUseCase;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

@Route
public class TriggerSchedulerRoute extends RouteBuilder {

    @Value("${scheduler-routes.trigger.cron-expression}")
    private String cronExpression;

    private final GetCampaignsByStatusUseCase getCampaignsByStatusUseCase;

    public TriggerSchedulerRoute(
            GetCampaignsByStatusUseCase getCampaignsByStatusUseCase
    ) {
        this.getCampaignsByStatusUseCase = getCampaignsByStatusUseCase;
    }

    @Override
    public void configure() {
        from(String.format("quartz://TriggerSchedulerRoute?cron=%s", cronExpression))
                .log("Route TriggerSchedulerRoute: starting new cycle")
                .process(exchange -> exchange.getIn().setBody(getCampaignsByStatusUseCase.execute(
                        GetCampaignsByStatusUseCase.Parameters.build(RTToolCampaign.Status.NOT_STARTED)
                )))
                .split(body())
                .to("seda:launchCampaigns");
    }
}
