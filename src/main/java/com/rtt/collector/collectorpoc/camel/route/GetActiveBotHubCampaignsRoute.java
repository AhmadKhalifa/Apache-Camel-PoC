package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GetActiveBotHubCampaignsUseCase;
import org.apache.camel.builder.RouteBuilder;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class GetActiveBotHubCampaignsRoute extends RouteBuilder {

    private final GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase;

    public GetActiveBotHubCampaignsRoute(GetActiveBotHubCampaignsUseCase getActiveBotHubCampaignsUseCase) {
        this.getActiveBotHubCampaignsUseCase = getActiveBotHubCampaignsUseCase;
    }

    @Override
    public void configure() {
        from("direct:getActiveBotHubCampaigns")
                .process(exchange -> exchange.getIn().setBody(getActiveBotHubCampaignsUseCase.execute(
                        GetActiveBotHubCampaignsUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID)
                        )
                )))
                .split(body())
                .to("seda:collectBotHubCampaign");
    }
}
