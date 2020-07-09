package com.rtt.collector.collectorpoc.campaign.rttool.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.service.RTToolCampaignService;
import lombok.Setter;

@UseCase
public class UpdateCampaignStatusUseCase
        extends BaseUseCase<RTToolCampaign, UpdateCampaignStatusUseCase.Parameters> {

    private final RTToolCampaignService rtToolCampaignService;

    public UpdateCampaignStatusUseCase(RTToolCampaignService rtToolCampaignService) {
        this.rtToolCampaignService = rtToolCampaignService;
    }

    @Override
    public RTToolCampaign execute(Parameters parameters) {
        return rtToolCampaignService.updateCampaignStatus(parameters.campaignId, parameters.status);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private long campaignId;
        private RTToolCampaign.Status status;

        private Parameters() {}

        public static Parameters build(long campaignId, RTToolCampaign.Status status) {
            return new Parameters() {{
                setCampaignId(campaignId);
                setStatus(status);
            }};
        }
    }
}
