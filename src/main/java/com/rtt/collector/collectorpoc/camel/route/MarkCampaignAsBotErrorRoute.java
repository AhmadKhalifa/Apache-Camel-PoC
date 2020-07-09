package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
import org.apache.camel.builder.RouteBuilder;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class MarkCampaignAsBotErrorRoute extends RouteBuilder {

    private final UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    public MarkCampaignAsBotErrorRoute(UpdateCampaignStatusUseCase updateCampaignStatusUseCase) {
        this.updateCampaignStatusUseCase = updateCampaignStatusUseCase;
    }

    @Override
    public void configure() {
        from("direct:markCampaignAsBotError")
                .process(exchange -> exchange.getIn().setBody(updateCampaignStatusUseCase.execute(
                        UpdateCampaignStatusUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID),
                                RTToolCampaign.Status.BOT_ERROR
                        )
                )))
                .to("direct:notifyCampaignMarkedAsBotError");

        from("direct:notifyCampaignMarkedAsBotError")
                .log("{body}");
    }
}
