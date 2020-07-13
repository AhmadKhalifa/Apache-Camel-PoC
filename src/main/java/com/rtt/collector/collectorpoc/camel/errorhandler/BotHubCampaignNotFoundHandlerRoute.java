package com.rtt.collector.collectorpoc.camel.errorhandler;

import com.rtt.collector.collectorpoc.annotation.Route;

@Route
public class BotHubCampaignNotFoundHandlerRoute extends GeneralExceptionHandlerRoute {

    public static final String ROUTE_ID = BotHubCampaignNotFoundHandlerRoute.class.getSimpleName();

    @Override
    public void configure() throws Exception {
        super.configure();

        from("direct:botHubCampaignNotFoundHandler")
                .routeId(ROUTE_ID)
                .log("BotHubCampaignNotFoundException caught: ${in.header.KEY_CAUGHT_EXCEPTION}");
    }
}
