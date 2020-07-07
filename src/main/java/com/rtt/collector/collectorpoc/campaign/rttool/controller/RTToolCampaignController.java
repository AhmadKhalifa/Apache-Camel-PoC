package com.rtt.collector.collectorpoc.campaign.rttool.controller;

import com.rtt.collector.collectorpoc.base.BaseController;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.usecase.GenerateRandomCampaignUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rttoolCampaign")
public class RTToolCampaignController extends BaseController {

    private final GenerateRandomCampaignUseCase generateRandomCampaignUseCase;

    public RTToolCampaignController(GenerateRandomCampaignUseCase generateRandomCampaignUseCase) {
        this.generateRandomCampaignUseCase = generateRandomCampaignUseCase;
    }

    @GetMapping("/{campaignName}/{msisdnsCount}")
    public ResponseEntity<RTToolCampaign> createCampaign(
            @PathVariable("campaignName") String campaignName,
            @PathVariable("msisdnsCount") int msisdnsCount
    ) {
        return ResponseEntity.ok(generateRandomCampaignUseCase.execute(
                GenerateRandomCampaignUseCase.Parameters.build(campaignName, msisdnsCount))
        );
    }
}
