package com.rtt.collector.collectorpoc.exception;

public class BotHubCampaignNotFoundException extends ResourceNotFoundException {

    public BotHubCampaignNotFoundException(long bothubCampaignId) {
        super(String.format("Bot hub campaign %d not found", bothubCampaignId));
    }

    public BotHubCampaignNotFoundException(String bothubCampaignId) {
        super(String.format("Bot hub campaign %s not found", bothubCampaignId));
    }
}
