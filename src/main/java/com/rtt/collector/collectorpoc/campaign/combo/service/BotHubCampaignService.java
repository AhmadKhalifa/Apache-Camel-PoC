package com.rtt.collector.collectorpoc.campaign.combo.service;

import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;

import java.util.List;

public interface BotHubCampaignService {

    void triggerCampaign(BotHubCampaign botHubCampaign);

    BotHubCampaign collectCampaignResults(long botHubCampaignId);

    List<BotHubCampaign> getActiveBotHubCampaigns(long rtToolCampaignId);
}
