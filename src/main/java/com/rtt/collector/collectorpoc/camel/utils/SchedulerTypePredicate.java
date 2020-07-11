package com.rtt.collector.collectorpoc.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class SchedulerTypePredicate implements Predicate {

    private final SchedulerType schedulerType;

    public SchedulerTypePredicate(SchedulerType schedulerType) {
        this.schedulerType = schedulerType;
    }

    @Override
    public boolean matches(Exchange exchange) {
        return exchange.getIn().getHeader(Constants.KEY_SCHEDULER_TYPE, SchedulerType.class) == schedulerType;
    }
}
