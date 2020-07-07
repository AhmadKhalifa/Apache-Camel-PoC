package com.rtt.collector.collectorpoc.campaign.combo.service;

import com.rtt.collector.collectorpoc.base.BaseService;
import com.rtt.collector.collectorpoc.campaign.combo.data.BotHubCampaignRepository;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignEntity;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignMapper;
import com.rtt.collector.collectorpoc.campaign.rttool.data.RTToolCampaignRepository;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignEntity;
import com.rtt.collector.collectorpoc.client.bothub.BotHubClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComboCampaignService extends BaseService implements BotHubCampaignService {

    private final BotHubClient botHubClient;
    private final BotHubCampaignRepository botHubCampaignRepository;
    private final RTToolCampaignRepository rtToolCampaignRepository;
    private final BotHubCampaignMapper botHubCampaignMapper;

    public ComboCampaignService(
            BotHubClient botHubClient,
            BotHubCampaignRepository botHubCampaignRepository,
            RTToolCampaignRepository rtToolCampaignRepository,
            BotHubCampaignMapper botHubCampaignMapper
    ) {
        this.botHubClient = botHubClient;
        this.botHubCampaignRepository = botHubCampaignRepository;
        this.rtToolCampaignRepository = rtToolCampaignRepository;
        this.botHubCampaignMapper = botHubCampaignMapper;
    }

    @Override
    public void triggerCampaign(BotHubCampaign botHubCampaign) {
        log.info(
                "{}- Triggering bot hub campaign #{}, Details: {}",
                Thread.currentThread().getName(),
                botHubCampaign.getId(),
                botHubCampaign
        );
        Optional<BotHubCampaignEntity> botHubCampaignEntityOptional = botHubCampaignRepository
                .findById(botHubCampaign.getId());
        if (botHubCampaignEntityOptional.isPresent()) {
            BotHubCampaignEntity botHubCampaignEntity = botHubCampaignEntityOptional.get();
            String botHubCampaignId = botHubClient.triggerCampaign(botHubCampaign);
            botHubCampaign.setBotHubId(botHubCampaignId);
            log.info(
                    "{}- Campaign #{} triggered successfully and has id of {}. Updating database...",
                    Thread.currentThread().getName(),
                    botHubCampaign.getId(),
                    botHubCampaignId
            );
            botHubCampaignEntity.setBotHubId(botHubCampaignId);
            botHubCampaignRepository.save(botHubCampaignEntity);
            log.info(
                    "{}- Bot hub campaign #{} was updated successfully in database",
                    Thread.currentThread().getName(),
                    botHubCampaign.getId()
            );
        } else {
            throw new RuntimeException(String.format("Bot hub campaign #%d not found", botHubCampaign.getId()));
        }
    }

    @Override
    public BotHubCampaign collectCampaignResults(long botHubCampaignId) {
        log.info(
                "{}- Collecting bot hub campaign with bot hub id of {}",
                Thread.currentThread().getName(),
                botHubCampaignId
        );

        Optional<BotHubCampaignEntity> botHubCampaignEntityOptional = botHubCampaignRepository
                .findById(botHubCampaignId);
        if (botHubCampaignEntityOptional.isPresent()) {
            BotHubCampaignEntity botHubCampaignEntity = botHubCampaignEntityOptional.get();
            BotHubCampaign botHubCampaign = botHubClient.collectBotHubCampaign(botHubCampaignEntity.getBotHubId());
            log.info(
                    "{}- Bot hub campaign #{} collected successfully. Details: {}. Updating database...",
                    Thread.currentThread().getName(),
                    botHubCampaign.getId(),
                    botHubCampaign
            );
            botHubCampaignEntity.setSentCount(botHubCampaign.getSentCount());
            botHubCampaignEntity.setDeliveredCount(botHubCampaign.getDeliveredCount());
            botHubCampaignEntity.setReadCount(botHubCampaign.getReadCount());
            botHubCampaignEntity.setErrorCount(botHubCampaign.getErrorCount());
            botHubCampaignRepository.save(botHubCampaignEntity);
            log.info(
                    "{}- Bot hub campaign #{} was updated successfully in database",
                    Thread.currentThread().getName(),
                    botHubCampaign.getId()
            );
            return botHubCampaign;
        } else {
            throw new RuntimeException(String.format("Bot hub campaign #%d not found", botHubCampaignId));
        }
    }

    @Override
    public List<BotHubCampaign> getActiveBotHubCampaigns(long rtToolCampaignId) {
        log.info(
                "{}- Finding active bot hub campaigns for RTTool campaign #{}...",
                Thread.currentThread().getName(),
                rtToolCampaignId
        );
        Optional<RTToolCampaignEntity> campaignEntityOptional = rtToolCampaignRepository.findById(rtToolCampaignId);
        if (campaignEntityOptional.isPresent()) {
            RTToolCampaignEntity rtToolCampaignEntity = campaignEntityOptional.get();
            List<BotHubCampaignEntity> botHubCampaigns = new ArrayList<>(rtToolCampaignEntity.getBotHubCampaigns());
            List<BotHubCampaign> activeBotHubCampaigns = botHubCampaigns
                    .stream()
                    .filter(botHubCampaign ->
                            botHubCampaign.getTotalCount() >
                                    botHubCampaign.getSentCount() + botHubCampaign.getErrorCount()
                    )
                    .map(botHubCampaignMapper::toDto)
                    .collect(Collectors.toList());

            if (activeBotHubCampaigns.isEmpty()) {
                log.info(
                        "{}- Campaign #{} has finished",
                        Thread.currentThread().getName(),
                        rtToolCampaignId
                );
                rtToolCampaignEntity.setStatus(RTToolCampaign.Status.FINISHED.ordinal());
                rtToolCampaignRepository.save(rtToolCampaignEntity);
            } else {
                log.info(
                        "{}- Found ({}) active bot hub campaigns for RTTool campaign #{}...",
                        Thread.currentThread().getName(),
                        activeBotHubCampaigns.size(),
                        rtToolCampaignId
                );
            }
            return activeBotHubCampaigns;
        } else {
            throw new RuntimeException(String.format("Campaign #%d not found", rtToolCampaignId));
        }
    }
}
