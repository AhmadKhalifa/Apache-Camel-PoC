package com.rtt.collector.collectorpoc.camel.errorhandler;

import com.rtt.collector.collectorpoc.annotation.Route;

@Route
public class RTTCampaignNotFoundHandlerRoute extends GeneralExceptionHandlerRoute {

    public static final String ROUTE_ID = RTTCampaignNotFoundHandlerRoute.class.getSimpleName();

    @Override
    public void configure() throws Exception {
        super.configure();

        from("direct:rttCampaignNotFoundHandler")
                .routeId(ROUTE_ID)
                .log("RTTCampaignNotFoundException caught: ${in.header.KEY_CAUGHT_EXCEPTION}");
    }
}
