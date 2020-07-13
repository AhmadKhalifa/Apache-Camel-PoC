package com.rtt.collector.collectorpoc.camel.errorhandler;

import com.rtt.collector.collectorpoc.annotation.Route;
import org.apache.camel.builder.RouteBuilder;

@Route
public abstract class GeneralExceptionHandlerRoute extends RouteBuilder {

    public static final String ROUTE_ID = GeneralExceptionHandlerRoute.class.getSimpleName();

    @Override
    public void configure() throws Exception {
        from("direct:generalExceptionHandler")
                .routeId(ROUTE_ID)
                .log("Exception caught: ${in.header.KEY_CAUGHT_EXCEPTION}");
    }
}
