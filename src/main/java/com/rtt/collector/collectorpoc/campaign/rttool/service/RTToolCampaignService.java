package com.rtt.collector.collectorpoc.campaign.rttool.service;

import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;

import java.util.List;

public interface RTToolCampaignService {

    List<RTToolCampaign> getAllCampaignsByStatus(RTToolCampaign.Status status);

    void updateCampaignStatus(long campaignId, RTToolCampaign.Status status);

    List<BotHubCampaign> chunkCampaign(long campaignId, int chunkSize);

    RTToolCampaign createCampaign(RTToolCampaign rtToolCampaign, List<String> msisdnNumbers, long botId);
}
