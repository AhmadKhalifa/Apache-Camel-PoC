package com.rtt.collector.collectorpoc.bot.service;

import com.rtt.collector.collectorpoc.bot.model.Bot;

public interface BotService {

    Bot getBotById(long botId);

    boolean validateBot(Bot bot);
}
