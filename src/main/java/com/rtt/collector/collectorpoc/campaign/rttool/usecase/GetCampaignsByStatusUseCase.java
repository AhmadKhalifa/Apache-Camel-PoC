package com.rtt.collector.collectorpoc.campaign.rttool.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.service.RTToolCampaignService;
import lombok.Setter;

import java.util.List;

@UseCase
public class GetCampaignsByStatusUseCase
        extends BaseUseCase<List<RTToolCampaign>, GetCampaignsByStatusUseCase.Parameters> {

    private final RTToolCampaignService rtToolCampaignService;

    public GetCampaignsByStatusUseCase(RTToolCampaignService rtToolCampaignService) {
        this.rtToolCampaignService = rtToolCampaignService;
    }

    @Override
    public List<RTToolCampaign> execute(Parameters parameters) {
        return rtToolCampaignService.getAllCampaignsByStatus(parameters.status);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private RTToolCampaign.Status status;

        private Parameters() {}

        public static Parameters build(RTToolCampaign.Status status) {
            return new Parameters() {{
                setStatus(status);
            }};
        }
    }
}
