package com.rtt.collector.collectorpoc.camel.schuedler;

import com.rtt.collector.collectorpoc.annotation.Scheduler;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import org.apache.camel.builder.RouteBuilder;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_SCHEDULER_TYPE;

@Scheduler
public class TriggerScheduler extends RouteBuilder {

    @Override
    public void configure() {
        from("quartz://triggerScheduler?cron={{scheduler-routes.trigger.cron-expression}}")
                .log("Trigger scheduler starting a new cycle")
                .setHeader(KEY_SCHEDULER_TYPE, constant(SchedulerType.TRIGGER))
                .setBody(constant(RTToolCampaign.Status.NOT_STARTED))
                .to("direct:getCampaignsByStatus");
    }
}
