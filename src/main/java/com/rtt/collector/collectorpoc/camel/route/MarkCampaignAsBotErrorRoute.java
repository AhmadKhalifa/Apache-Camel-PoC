package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class MarkCampaignAsBotErrorRoute extends BaseRoute {

    public static final String ROUTE_ID = MarkCampaignAsBotErrorRoute.class.getSimpleName();

    private final UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    public MarkCampaignAsBotErrorRoute(UpdateCampaignStatusUseCase updateCampaignStatusUseCase) {
        this.updateCampaignStatusUseCase = updateCampaignStatusUseCase;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        from("direct:markCampaignAsBotError")
                .routeId(ROUTE_ID)
                .process(exchange -> exchange.getIn().setBody(updateCampaignStatusUseCase.execute(
                        UpdateCampaignStatusUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID),
                                RTToolCampaign.Status.BOT_ERROR
                        )
                )))
                .to("direct:notifyCampaignMarkedAsBotError");

        from("direct:notifyCampaignMarkedAsBotError")
                .log("Campaign marked as bot error: ${body}");
    }
}
