package com.rtt.collector.collectorpoc.client.bothub;

import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;

public interface BotHubClient {

    boolean validateBot(String botId);

    String triggerCampaign(BotHubCampaign botHubCampaign);

    BotHubCampaign collectBotHubCampaign(String botHubCampaignId);
}
