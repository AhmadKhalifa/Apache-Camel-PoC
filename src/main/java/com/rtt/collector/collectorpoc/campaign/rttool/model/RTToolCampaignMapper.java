package com.rtt.collector.collectorpoc.campaign.rttool.model;

import com.rtt.collector.collectorpoc.annotation.Mapper;
import com.rtt.collector.collectorpoc.base.ObjectMapper;
import com.rtt.collector.collectorpoc.bot.model.BotMapper;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignMapper;
import com.rtt.collector.collectorpoc.campaign.msisdn.MsisdnMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper
public class RTToolCampaignMapper extends ObjectMapper<RTToolCampaign, RTToolCampaignEntity> {

    private BotMapper botMapper;
    private BotHubCampaignMapper botHubCampaignMapper;
    private MsisdnMapper msisdnMapper;

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Autowired
    public void setRtToolCampaignMapper(BotHubCampaignMapper botHubCampaignMapper) {
        this.botHubCampaignMapper = botHubCampaignMapper;
    }

    @Autowired
    public void setMsisdnMapper(MsisdnMapper msisdnMapper) {
        this.msisdnMapper = msisdnMapper;
    }

    @Override
    public RTToolCampaign toDto(RTToolCampaignEntity entity, boolean eager) {
        if (Objects.isNull(entity)) return null;
        RTToolCampaign dto = new RTToolCampaign();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStatus(RTToolCampaign.Status.values()[entity.getStatus()]);
        if (eager) {
            dto.setBot(botMapper.toDto(entity.getBot(), false));
            if (entity.getBotHubCampaigns() == null) {
                entity.setBotHubCampaigns(new HashSet<>());
            }
            dto.setBotHubCampaigns(entity.getBotHubCampaigns()
                    .stream()
                    .map(botHubCampaign -> botHubCampaignMapper.toDto(botHubCampaign, false))
                    .collect(Collectors.toList())
            );
            if (entity.getMsisdns() == null) {
                entity.setMsisdns(new HashSet<>());
            }
            dto.setMsisdns(entity.getMsisdns()
                    .stream()
                    .map(msisdn -> msisdnMapper.toDto(msisdn, false))
                    .collect(Collectors.toList())
            );
        }
        return dto;
    }

    @Override
    public RTToolCampaignEntity toEntity(RTToolCampaign dto, boolean eager) {
        RTToolCampaignEntity entity = new RTToolCampaignEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus().ordinal());
        if (eager) {
            entity.setBot(botMapper.toEntity(dto.getBot(), false));
            if (dto.getBotHubCampaigns() == null) {
                dto.setBotHubCampaigns(new ArrayList<>());
            }
            entity.setBotHubCampaigns(dto.getBotHubCampaigns()
                    .stream()
                    .map(botHubCampaign -> botHubCampaignMapper.toEntity(botHubCampaign, false))
                    .collect(Collectors.toSet())
            );
            if (dto.getMsisdns() == null) {
                dto.setMsisdns(new ArrayList<>());
            }
            entity.setMsisdns(dto.getMsisdns()
                    .stream()
                    .map(msisdn -> msisdnMapper.toEntity(msisdn, false))
                    .collect(Collectors.toSet())
            );
        }
        return entity;
    }
}
