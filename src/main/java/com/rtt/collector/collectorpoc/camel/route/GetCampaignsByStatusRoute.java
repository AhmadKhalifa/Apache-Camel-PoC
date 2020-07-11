package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerTypePredicate;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetCampaignsByStatusUseCase;
import org.apache.camel.builder.RouteBuilder;

@Route
public class GetCampaignsByStatusRoute extends RouteBuilder {

    private final GetCampaignsByStatusUseCase getCampaignsByStatusUseCase;

    public GetCampaignsByStatusRoute(GetCampaignsByStatusUseCase getCampaignsByStatusUseCase) {
        this.getCampaignsByStatusUseCase = getCampaignsByStatusUseCase;
    }

    @Override
    public void configure() {
        from("direct:getCampaignsByStatus")
                .process(exchange -> exchange.getIn().setBody(getCampaignsByStatusUseCase.execute(
                        GetCampaignsByStatusUseCase.Parameters.build(
                                exchange.getIn().getBody(RTToolCampaign.Status.class)
                        )
                )))
                .split(body())
                .choice()
                    .when(new SchedulerTypePredicate(SchedulerType.TRIGGER))
                        .to("seda:launchCampaigns")
                    .when(new SchedulerTypePredicate(SchedulerType.COLLECTOR))
                        .to("seda:collectRTToolCampaigns")
                    .otherwise()
                        .log("Scheduler not supported")
                .end();
    }
}