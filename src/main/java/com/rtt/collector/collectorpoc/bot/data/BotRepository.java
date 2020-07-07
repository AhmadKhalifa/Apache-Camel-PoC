package com.rtt.collector.collectorpoc.bot.data;

import com.rtt.collector.collectorpoc.bot.model.BotEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepository extends CrudRepository<BotEntity, Long> {

}
