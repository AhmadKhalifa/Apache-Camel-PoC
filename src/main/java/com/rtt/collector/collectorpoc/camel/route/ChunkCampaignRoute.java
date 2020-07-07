package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.ChunkCampaignUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;
import org.apache.camel.builder.RouteBuilder;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class ChunkCampaignRoute extends RouteBuilder {

    private final ChunkCampaignUseCase chunkCampaignUseCase;
    private final UpdateCampaignStatusUseCase updateCampaignStatusUseCase;

    public ChunkCampaignRoute(
            ChunkCampaignUseCase chunkCampaignUseCase,
            UpdateCampaignStatusUseCase updateCampaignStatusUseCase
    ) {
        this.chunkCampaignUseCase = chunkCampaignUseCase;
        this.updateCampaignStatusUseCase = updateCampaignStatusUseCase;
    }

    @Override
    public void configure() {
        from("direct:chunkCampaign")
                .process(exchange -> updateCampaignStatusUseCase.execute(
                        UpdateCampaignStatusUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID),
                                RTToolCampaign.Status.ACTIVE
                        )
                ))
                .process(exchange -> exchange.getIn().setBody(chunkCampaignUseCase.execute(
                        ChunkCampaignUseCase.Parameters.build((long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID))
                )))
                .split(body())
                .to("seda:triggerCampaign");
    }
}
