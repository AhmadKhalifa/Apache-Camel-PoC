package com.rtt.collector.collectorpoc.campaign.rttool.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.service.BotHubCampaignService;
import lombok.Setter;

import java.util.List;

@UseCase
public class GetActiveBotHubCampaignsUseCase
        extends BaseUseCase<List<BotHubCampaign>, GetActiveBotHubCampaignsUseCase.Parameters> {

    private final BotHubCampaignService botHubCampaignService;

    public GetActiveBotHubCampaignsUseCase(BotHubCampaignService botHubCampaignService) {
        this.botHubCampaignService = botHubCampaignService;
    }

    @Override
    public List<BotHubCampaign> execute(Parameters parameters) {
        return botHubCampaignService.getActiveBotHubCampaigns(parameters.rtToolCampaignId);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private long rtToolCampaignId;

        private Parameters() {}

        public static Parameters build(long rtToolCampaignId) {
            return new Parameters() {{
                setRtToolCampaignId(rtToolCampaignId);
            }};
        }
    }
}