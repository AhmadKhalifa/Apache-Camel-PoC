package com.rtt.collector.collectorpoc.client.bothub;

import com.rtt.collector.collectorpoc.annotation.Client;
import com.rtt.collector.collectorpoc.base.BaseHttpClient;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Client
public class ComboClient extends BaseHttpClient implements BotHubClient {

    public final ArrayList<BotHubCampaign> triggeredCampaigns = new ArrayList<>();

    @Override
    public boolean validateBot(String botId) {
        return new Random().nextInt(100) > 5;
    }

    @Override
    public String triggerCampaign(BotHubCampaign botHubCampaign) {
        String botHubCampaignId = UUID.randomUUID().toString();
        botHubCampaign.setBotHubId(botHubCampaignId);
        triggeredCampaigns.add(botHubCampaign);
        return botHubCampaignId;
    }

    @Override
    public BotHubCampaign collectBotHubCampaign(String botHubCampaignId) {
        return getCampaignWithNewStatistics(botHubCampaignId);
    }

    public BotHubCampaign getCampaignWithNewStatistics(String botHubCampaignId) {
        BotHubCampaign botHubCampaign = triggeredCampaigns.stream()
                .filter(campaign -> botHubCampaignId.equals(campaign.getBotHubId()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        Random random = new Random();
        if (isActive(botHubCampaign)) {
            botHubCampaign.setSentCount(
                    Math.max(botHubCampaign.getSentCount(), random.nextInt(botHubCampaign.getTotalCount() + 1))
            );
        }
        if (isActive(botHubCampaign)) {
            botHubCampaign.setDeliveredCount(
                    Math.max(botHubCampaign.getDeliveredCount(), random.nextInt(botHubCampaign.getTotalCount() + 1))
            );
        }
        if (isActive(botHubCampaign)) {
            botHubCampaign.setReadCount(
                    Math.max(botHubCampaign.getReadCount(), random.nextInt(botHubCampaign.getTotalCount() + 1))
            );
        }
        if (isActive(botHubCampaign)) {
            botHubCampaign.setErrorCount(
                    Math.max(botHubCampaign.getErrorCount(), random.nextInt(botHubCampaign.getTotalCount() + 1))
            );
        }
        return botHubCampaign;
    }

    private boolean isActive(BotHubCampaign botHubCampaign) {
        return botHubCampaign.getTotalCount() != botHubCampaign.getReadCount() + botHubCampaign.getErrorCount();
    }
}
