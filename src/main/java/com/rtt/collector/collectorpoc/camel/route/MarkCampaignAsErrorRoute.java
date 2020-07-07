package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
import org.apache.camel.builder.RouteBuilder;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class MarkCampaignAsErrorRoute extends RouteBuilder {

    private final UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    public MarkCampaignAsErrorRoute(UpdateCampaignStatusUseCase updateCampaignStatusUseCase) {
        this.updateCampaignStatusUseCase = updateCampaignStatusUseCase;
    }

    @Override
    public void configure() {
        from("direct:markCampaignAsBotError")
                .process(exchange -> updateCampaignStatusUseCase.execute(
                        UpdateCampaignStatusUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID),
                                RTToolCampaign.Status.BOT_ERROR
                        )
                ));
    }
}
