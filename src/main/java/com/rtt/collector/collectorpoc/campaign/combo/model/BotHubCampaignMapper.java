package com.rtt.collector.collectorpoc.campaign.combo.model;

import com.rtt.collector.collectorpoc.annotation.Mapper;
import com.rtt.collector.collectorpoc.base.ObjectMapper;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Mapper
public class BotHubCampaignMapper extends ObjectMapper<BotHubCampaign, BotHubCampaignEntity> {

    private RTToolCampaignMapper rtToolCampaignMapper;

    @Autowired
    public void setRtToolCampaignMapper(RTToolCampaignMapper rtToolCampaignMapper) {
        this.rtToolCampaignMapper = rtToolCampaignMapper;
    }

    @Override
    public BotHubCampaign toDto(BotHubCampaignEntity entity, boolean eager) {
        if (Objects.isNull(entity)) return null;
        return new BotHubCampaign() {{
            setId(entity.getId());
            setSentCount(entity.getSentCount());
            setDeliveredCount(entity.getDeliveredCount());
            setReadCount(entity.getReadCount());
            setTotalCount(entity.getTotalCount());
            setErrorCount(entity.getErrorCount());
            setBotHubId(entity.getBotHubId());
            if (eager) {
                setRtToolCampaign(rtToolCampaignMapper.toDto(entity.getRtToolCampaign(), false));
            }
        }};
    }

    @Override
    public BotHubCampaignEntity toEntity(BotHubCampaign dto, boolean eager) {
        if (Objects.isNull(dto)) return null;
        BotHubCampaignEntity entity = new BotHubCampaignEntity();
        entity.setId(dto.getId());
        entity.setSentCount(dto.getSentCount());
        entity.setDeliveredCount(dto.getDeliveredCount());
        entity.setReadCount(dto.getReadCount());
        entity.setTotalCount(dto.getTotalCount());
        entity.setErrorCount(dto.getErrorCount());
        entity.setBotHubId(dto.getBotHubId());
        if (eager) {
            entity.setRtToolCampaign(rtToolCampaignMapper.toEntity(dto.getRtToolCampaign(), false));
        }
        return entity;
    }
}
