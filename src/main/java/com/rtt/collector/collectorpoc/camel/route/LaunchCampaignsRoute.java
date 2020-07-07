package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class LaunchCampaignsRoute extends RouteBuilder {

    @Value("${scheduler-routes.trigger.thread-pool}")
    private int threadPool;

    private final ValidateBotUseCase validateBotUseCase;

    public LaunchCampaignsRoute(ValidateBotUseCase validateBotUseCase) {
        this.validateBotUseCase = validateBotUseCase;
    }

    @Override
    public void configure() {
        from(String.format("seda:launchCampaigns?concurrentConsumers=%d", threadPool))
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
