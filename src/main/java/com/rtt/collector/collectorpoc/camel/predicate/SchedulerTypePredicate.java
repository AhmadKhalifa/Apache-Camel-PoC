package com.rtt.collector.collectorpoc.camel.predicate;

import com.rtt.collector.collectorpoc.camel.utils.Constants;
import com.rtt.collector.collectorpoc.camel.utils.SchedulerType;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class SchedulerTypePredicate implements Predicate {

    private final SchedulerType schedulerType;

    public static SchedulerTypePredicate is(SchedulerType schedulerType) {
        return new SchedulerTypePredicate(schedulerType);
    }

    private SchedulerTypePredicate(SchedulerType schedulerType) {
        this.schedulerType = schedulerType;
    }

    @Override
    public boolean matches(Exchange exchange) {
        return exchange.getIn().getHeader(Constants.KEY_SCHEDULER_TYPE, SchedulerType.class) == schedulerType;
    }
}
