package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KET_BOT_HUB_BOT_ID;
import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class CollectRTToolCampaignsRoute extends RouteBuilder {

    @Value("${scheduler-routes.collector.thread-pool}")
    private int threadPool;

    private final ValidateBotUseCase validateBotUseCase;

    public CollectRTToolCampaignsRoute(ValidateBotUseCase validateBotUseCase) {
        this.validateBotUseCase = validateBotUseCase;
    }

    @Override
    public void configure() {
        from(String.format("seda:collectRTToolCampaigns?concurrentConsumers=%d", threadPool))
                .process(exchange -> {
                    RTToolCampaign rtToolCampaign = exchange.getIn().getBody(RTToolCampaign.class);
                    long campaignId = rtToolCampaign.getId();
                    exchange.getIn().setHeader(KEY_RTTOOL_CAMPAIGN_ID, campaignId);
                    exchange.getIn().setHeader(KET_BOT_HUB_BOT_ID, rtToolCampaign.getBot().getBotHubId());
                    exchange.getIn().setBody(validateBotUseCase.execute(
                            ValidateBotUseCase.Parameters.build(rtToolCampaign.getBot())
                    ));
                })
                .choice()
                    .when(body().isEqualTo(true)).to("direct:getActiveBotHubCampaigns")
                    .otherwise().to("direct:markCampaignAsBotError")
                .end();
    }
}
