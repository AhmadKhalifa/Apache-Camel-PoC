package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetActiveBotHubCampaignsUseCase;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class GetActiveBotHubCampaignsRoute extends BaseRoute {

    public static final String ROUTE_ID = GetActiveBotHubCampaignsRoute.class.getSimpleName();

    private final GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase;

    public GetActiveBotHubCampaignsRoute(GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase) {
        this.getActiveBotHubCampaignsUseCase = getActiveBotHubCampaignsUseCase;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        from("direct:getActiveBotHubCampaigns")
                .routeId(ROUTE_ID)
                .process(exchange -> exchange.getIn().setBody(getActiveBotHubCampaignsUseCase.execute(
                        GetActiveBotHubCampaignsUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID)
                        )
                )))
                .split(body())
                .to("seda:collectBotHubCampaign");
    }
}
