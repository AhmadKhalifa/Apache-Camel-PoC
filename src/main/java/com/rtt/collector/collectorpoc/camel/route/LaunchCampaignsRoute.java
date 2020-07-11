package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class LaunchCampaignsRoute extends BaseRoute {

    public static final String ROUTE_ID = LaunchCampaignsRoute.class.getSimpleName();

    private final ValidateBotUseCase validateBotUseCase;

    public LaunchCampaignsRoute(ValidateBotUseCase validateBotUseCase) {
        this.validateBotUseCase = validateBotUseCase;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        from("seda:launchCampaigns?concurrentConsumers={{scheduler-routes.trigger.thread-pool}}")
                .routeId(ROUTE_ID)
                .process(exchange -> {
                    RTToolCampaign rtToolCampaign = exchange.getIn().getBody(RTToolCampaign.class);
                    long campaignId = rtToolCampaign.getId();
                    exchange.getIn().setHeader(KEY_RTTOOL_CAMPAIGN_ID, campaignId);
                    exchange.getIn().setBody(validateBotUseCase.execute(
                            ValidateBotUseCase.Parameters.build(rtToolCampaign.getBot())
                    ));
                })
                .choice()
                .when(body().isEqualTo(true))
                    .to("direct:chunkCampaign")
                .otherwise()
                    .to("direct:markCampaignAsBotError")
                .end();
    }
}
