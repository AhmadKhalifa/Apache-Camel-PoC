package com.rtt.collector.collectorpoc.bot.model;

import com.rtt.collector.collectorpoc.annotation.Mapper;
import com.rtt.collector.collectorpoc.base.ObjectMapper;

import java.util.Objects;

@Mapper
public class BotMapper extends ObjectMapper<Bot, BotEntity> {

    @Override
    public Bot toDto(BotEntity entity, boolean eager) {
        if (Objects.isNull(entity)) return null;
        return new Bot() {{
            setId(entity.getId());
            setName(entity.getName());
            setBotHubId(entity.getBotHubId());
            setActivated(entity.isActivated());
            setError(entity.isError());
        }};
    }

    @Override
    public BotEntity toEntity(Bot dto, boolean eager) {
        if (Objects.isNull(dto)) return null;
        BotEntity entity = new BotEntity();
        entity.setBotHubId(dto.getBotHubId());
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setActivated(dto.isActivated());
        entity.setError(dto.isError());
        return entity;
    }
}
