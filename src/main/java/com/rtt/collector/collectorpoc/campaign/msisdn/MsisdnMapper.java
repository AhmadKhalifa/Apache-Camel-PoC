package com.rtt.collector.collectorpoc.campaign.msisdn;

import com.rtt.collector.collectorpoc.annotation.Mapper;
import com.rtt.collector.collectorpoc.base.ObjectMapper;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public class MsisdnMapper extends ObjectMapper<Msisdn, MsisdnEntity> {

    private RTToolCampaignMapper rtToolCampaignMapper;

    @Autowired
    public void setRtToolCampaignMapper(RTToolCampaignMapper rtToolCampaignMapper) {
        this.rtToolCampaignMapper = rtToolCampaignMapper;
    }

    @Override
    public Msisdn toDto(MsisdnEntity entity, boolean eager) {
        return new Msisdn() {{
            setId(entity.getId());
            setNumber(entity.getNumber());
            if (eager) {
                setRtToolCampaign(rtToolCampaignMapper.toDto(entity.getRtToolCampaign(), false));
            }
        }};
    }

    @Override
    public MsisdnEntity toEntity(Msisdn dto, boolean eager) {
        MsisdnEntity entity = new MsisdnEntity();
        entity.setId(dto.getId());
        entity.setNumber(dto.getNumber());
        if (eager) {
            entity.setRtToolCampaign(rtToolCampaignMapper.toEntity(dto.getRtToolCampaign(), false));
        }
        return entity;
    }
}
