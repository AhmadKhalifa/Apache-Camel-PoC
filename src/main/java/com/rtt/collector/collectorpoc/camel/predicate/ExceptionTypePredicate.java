package com.rtt.collector.collectorpoc.camel.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_CAUGHT_EXCEPTION;

public class ExceptionTypePredicate implements Predicate {

    private final Class<? extends Exception> clazz;

    public static ExceptionTypePredicate is(Class<? extends Exception> clazz) {
        return new ExceptionTypePredicate(clazz);
    }

    private ExceptionTypePredicate(Class<? extends Exception> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean matches(Exchange exchange) {
        Exception caughtException = exchange.getIn().getHeader(KEY_CAUGHT_EXCEPTION, Exception.class);
        return clazz.isInstance(caughtException);
    }
}
