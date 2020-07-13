package com.rtt.collector.collectorpoc.campaign.rttool.service;

import com.rtt.collector.collectorpoc.base.BaseService;
import com.rtt.collector.collectorpoc.bot.data.BotRepository;
import com.rtt.collector.collectorpoc.bot.model.BotEntity;
import com.rtt.collector.collectorpoc.bot.model.BotMapper;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignEntity;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignMapper;
import com.rtt.collector.collectorpoc.campaign.msisdn.MsisdnEntity;
import com.rtt.collector.collectorpoc.campaign.rttool.data.RTToolCampaignRepository;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignEntity;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignMapper;
import com.rtt.collector.collectorpoc.exception.BotNotFoundException;
import com.rtt.collector.collectorpoc.exception.RTTCampaignNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComboRTToolCampaignService extends BaseService implements RTToolCampaignService {

    private final RTToolCampaignRepository rtToolCampaignRepository;
    private final BotRepository botRepository;
    private final RTToolCampaignMapper rtToolCampaignMapper;
    private final BotMapper botMapper;
    private final BotHubCampaignMapper botHubCampaignMapper;

    public ComboRTToolCampaignService(
            RTToolCampaignRepository rtToolCampaignRepository,
            BotRepository botRepository,
            RTToolCampaignMapper rtToolCampaignMapper,
            BotMapper botMapper,
            BotHubCampaignMapper botHubCampaignMapper) {
        this.rtToolCampaignRepository = rtToolCampaignRepository;
        this.botRepository = botRepository;
        this.rtToolCampaignMapper = rtToolCampaignMapper;
        this.botMapper = botMapper;
        this.botHubCampaignMapper = botHubCampaignMapper;
    }

    @Override
    public List<RTToolCampaign> getAllCampaignsByStatus(RTToolCampaign.Status status) {
        log.info(
                "{}- Finding {} RTTool campaigns...",
                Thread.currentThread().getName(),
                status
        );
        List<RTToolCampaign> activeCampaigns = rtToolCampaignRepository
                .findAllByStatus(status.ordinal())
                .stream()
                .map(entity -> {
                    RTToolCampaign campaign = rtToolCampaignMapper.toDto(entity, false);
                    campaign.setBot(botMapper.toDto(entity.getBot()));
                    return campaign;
                })
                .collect(Collectors.toList());
        log.info(
                "{}- Found ({}) {} RTTool campaigns",
                Thread.currentThread().getName(),
                activeCampaigns.size(),
                status
        );
        return activeCampaigns;
    }

    @Override
    public RTToolCampaign updateCampaignStatus(long campaignId, RTToolCampaign.Status status) {
        log.info("{}- Requesting RTTool campaign #{} ...", Thread.currentThread().getName(), campaignId);
        Optional<RTToolCampaignEntity> rtToolCampaignOptional = rtToolCampaignRepository.findById(campaignId);
        if (rtToolCampaignOptional.isPresent()) {
            RTToolCampaignEntity rtToolCampaignEntity = rtToolCampaignOptional.get();
            rtToolCampaignEntity.setStatus(status.ordinal());
            rtToolCampaignEntity = rtToolCampaignRepository.save(rtToolCampaignEntity);
            log.info(
                    "{}- Updated RTTool campaign #{} status to {}",
                    Thread.currentThread().getName(),
                    campaignId,
                    status
            );
            return rtToolCampaignMapper.toDto(rtToolCampaignEntity);
        } else {
            throw new RTTCampaignNotFoundException(campaignId);
        }
    }

    @Override
    public List<BotHubCampaign> chunkCampaign(long campaignId, int chunkSize) {
        log.info(
                "{}- Chunking RTTool campaign #{} to chunks of size ({}) ...",
                Thread.currentThread().getName(),
                campaignId,
                chunkSize
        );
        Optional<RTToolCampaignEntity> rtToolCampaignOptional = rtToolCampaignRepository.findById(campaignId);
        if (rtToolCampaignOptional.isPresent()) {
            RTToolCampaignEntity rtToolCampaignEntity = rtToolCampaignOptional.get();
            List<MsisdnEntity> msisdnEntities = new ArrayList<>(rtToolCampaignEntity.getMsisdns());
            AtomicInteger counter = new AtomicInteger();
            List<BotHubCampaignEntity> botHubCampaigns = new ArrayList<>(
                    msisdnEntities
                            .stream()
                            .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                            .values()
            )
                    .stream()
                    .map(msisdnsChunk -> BotHubCampaignEntity.builder()
                            .rtToolCampaign(rtToolCampaignEntity)
                            .sentCount(0)
                            .readCount(0)
                            .errorCount(0)
                            .deliveredCount(0)
                            .totalCount(msisdnsChunk.size())
                            .build()
                    )
                    .collect(Collectors.toList());
            rtToolCampaignEntity.setBotHubCampaigns(new HashSet<>(botHubCampaigns));
            List<BotHubCampaign> campaigns = rtToolCampaignRepository.save(rtToolCampaignEntity)
                    .getBotHubCampaigns()
                    .stream()
                    .map(botHubCampaignMapper::toDto)
                    .collect(Collectors.toList());
            log.info(
                    "{}- RTTool campaign #{} was chunked into ({}) chunks to chunks of size ({}) ...",
                    Thread.currentThread().getName(),
                    campaignId,
                    botHubCampaigns.size(),
                    chunkSize
            );
            return campaigns;
        } else {
            throw new RTTCampaignNotFoundException(campaignId);
        }
    }

    @Override
    @Transactional
    public RTToolCampaign createCampaign(RTToolCampaign rtToolCampaign, List<String> msisdnNumbers, long botId) {
        Optional<BotEntity> botEntityOptional = botRepository.findById(botId);
        if (botEntityOptional.isPresent()) {
            BotEntity botEntity = botEntityOptional.get();
            RTToolCampaignEntity campaignEntity = rtToolCampaignMapper.toEntity(rtToolCampaign);
            campaignEntity.setBot(botEntity);
            final RTToolCampaignEntity savedCampaignEntity = rtToolCampaignRepository.save(campaignEntity);
            List<MsisdnEntity> msisdnEntities = msisdnNumbers.stream()
                    .map(msisdnNumber ->
                            MsisdnEntity.builder().number(msisdnNumber).rtToolCampaign(savedCampaignEntity).build()
                    )
                    .collect(Collectors.toList());
            savedCampaignEntity.setMsisdns(new HashSet<>(msisdnEntities));
            return rtToolCampaignMapper.toDto(rtToolCampaignRepository.save(savedCampaignEntity));
        } else {
            throw new BotNotFoundException(botId);
        }
    }
}
