package com.rtt.collector.collectorpoc.campaign.combo.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.service.BotHubCampaignService;
import lombok.Setter;

@UseCase
public class TriggerBotHubCampaignUseCase
        extends BaseUseCase<BotHubCampaign, TriggerBotHubCampaignUseCase.Parameters> {

    private final BotHubCampaignService botHubCampaignService;

    public TriggerBotHubCampaignUseCase(BotHubCampaignService botHubCampaignService) {
        this.botHubCampaignService = botHubCampaignService;
    }

    @Override
    public BotHubCampaign execute(Parameters parameters) {
        return botHubCampaignService.triggerCampaign(parameters.botHubCampaign);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private BotHubCampaign botHubCampaign;

        private Parameters() {}

        public static Parameters build(BotHubCampaign botHubCampaign) {
            return new Parameters() {{
                setBotHubCampaign(botHubCampaign);
            }};
        }
    }
}
