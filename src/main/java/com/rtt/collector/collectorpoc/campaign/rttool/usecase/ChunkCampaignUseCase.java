package com.rtt.collector.collectorpoc.campaign.rttool.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.service.RTToolCampaignService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@UseCase
public class ChunkCampaignUseCase
        extends BaseUseCase<List<BotHubCampaign>, ChunkCampaignUseCase.Parameters> {

    private final RTToolCampaignService rtToolCampaignService;

    @Value("${rtt.trigger.chunk-size}")
    private int chunkSize;

    public ChunkCampaignUseCase(RTToolCampaignService rtToolCampaignService) {
        this.rtToolCampaignService = rtToolCampaignService;
    }

    @Override
    public List<BotHubCampaign> execute(Parameters parameters) {
        return rtToolCampaignService.chunkCampaign(parameters.campaignId, chunkSize);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private long campaignId;

        private Parameters() {}

        public static Parameters build(long campaignId) {
            return new Parameters() {{
                setCampaignId(campaignId);
            }};
        }
    }
}