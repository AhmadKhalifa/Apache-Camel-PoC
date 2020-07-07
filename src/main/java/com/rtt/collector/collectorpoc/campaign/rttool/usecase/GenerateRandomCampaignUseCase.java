package com.rtt.collector.collectorpoc.campaign.rttool.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.service.RTToolCampaignService;
import com.rtt.collector.collectorpoc.util.MsisdnGenerator;
import lombok.Setter;

import java.util.Random;

@UseCase
public class GenerateRandomCampaignUseCase
        extends BaseUseCase<RTToolCampaign, GenerateRandomCampaignUseCase.Parameters> {

    private final RTToolCampaignService rtToolCampaignService;
    private final MsisdnGenerator msisdnGenerator;

    public GenerateRandomCampaignUseCase(
            RTToolCampaignService rtToolCampaignService,
            MsisdnGenerator msisdnGenerator
    ) {
        this.rtToolCampaignService = rtToolCampaignService;
        this.msisdnGenerator = msisdnGenerator;
    }

    @Override
    public RTToolCampaign execute(Parameters parameters) {
        return rtToolCampaignService.createCampaign(
                RTToolCampaign.builder()
                        .name(parameters.campaignName)
                        .status(RTToolCampaign.Status.NOT_STARTED)
                        .build(),
                msisdnGenerator.generate(parameters.msisdnsCount),
                (1 + new Random().nextInt(3))
        );
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private String campaignName;
        private int msisdnsCount;

        private Parameters() {
        }

        public static Parameters build(String campaignName, int msisdnsCount) {
            return new Parameters() {{
                setCampaignName(campaignName);
                setMsisdnsCount(msisdnsCount);
            }};
        }
    }
}