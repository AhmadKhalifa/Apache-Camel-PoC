package com.rtt.collector.collectorpoc.campaign.combo.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.service.BotHubCampaignService;
import lombok.Setter;

@UseCase
public class CollectBotHubCampaignResultsUseCase
        extends BaseUseCase<BotHubCampaign, CollectBotHubCampaignResultsUseCase.Parameters> {

    private final BotHubCampaignService botHubCampaignService;

    public CollectBotHubCampaignResultsUseCase(BotHubCampaignService botHubCampaignService) {
        this.botHubCampaignService = botHubCampaignService;
    }

    @Override
    public BotHubCampaign execute(Parameters parameters) {
        return botHubCampaignService.collectCampaignResults(parameters.botHubCampaignId);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private long botHubCampaignId;

        private Parameters() {}

        public static Parameters build(long botHubCampaignId) {
            return new Parameters() {{
                setBotHubCampaignId(botHubCampaignId);
            }};
        }
    }
}
