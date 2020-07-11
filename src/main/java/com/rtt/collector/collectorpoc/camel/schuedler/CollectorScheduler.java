package com.rtt.collector.collectorpoc.camel.schuedler;

import com.rtt.collector.collectorpoc.annotation.Scheduler;
import com.rtt.collector.collectorpoc.base.BaseScheduler;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_SCHEDULER_TYPE;

@Scheduler
public class CollectorScheduler extends BaseScheduler {

    public static final String ROUTE_ID = CollectorScheduler.class.getSimpleName();

    @Override
    public void configure() throws Exception {
        super.configure();
        from("quartz://collectorScheduler?cron={{scheduler-routes.collector.cron-expression}}")
                .routeId(ROUTE_ID)
                .log("Collector scheduler starting a new cycle")
                .setHeader(KEY_SCHEDULER_TYPE, constant(SchedulerType.COLLECTOR))
                .setBody(constant(RTToolCampaign.Status.ACTIVE))
                .to("direct:getCampaignsByStatus");
    }
}
