package com.rtt.collector.collectorpoc.camel.route;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.base.BaseRoute;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.ChunkCampaignUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.UpdateCampaignStatusUseCase;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;

@Route
public class ChunkCampaignRoute extends BaseRoute {

    public static final String ROUTE_ID = ChunkCampaignRoute.class.getSimpleName();

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
    public void configure() throws Exception {
        super.configure();
        from("direct:chunkCampaign")
                .routeId(ROUTE_ID)
                .process(exchange -> exchange.getIn().setBody(updateCampaignStatusUseCase.execute(
                        UpdateCampaignStatusUseCase.Parameters.build(
                                (long) exchange.getIn().getHeader(KEY_RTTOOL_CAMPAIGN_ID),
                                RTToolCampaign.Status.ACTIVE
                        )
                )))
                .process(exchange -> exchange.getIn().setBody(chunkCampaignUseCase.execute(
                        ChunkCampaignUseCase.Parameters.build(exchange.getIn().getBody(RTToolCampaign.class).getId())
                )))
                .split(body())
                .to("seda:triggerCampaign");
    }
}
