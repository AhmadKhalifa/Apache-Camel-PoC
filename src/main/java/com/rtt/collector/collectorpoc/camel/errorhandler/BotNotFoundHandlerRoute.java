package com.rtt.collector.collectorpoc.camel.errorhandler;

import com.rtt.collector.collectorpoc.annotation.Route;

@Route
public class BotNotFoundHandlerRoute extends GeneralExceptionHandlerRoute {

    public static final String ROUTE_ID = BotNotFoundHandlerRoute.class.getSimpleName();

    @Override
    public void configure() throws Exception {
        super.configure();

        from("direct:botNotFoundHandler")
                .routeId(ROUTE_ID)
                .log("BotNotFoundException caught: ${in.header.KEY_CAUGHT_EXCEPTION}");
    }
}
