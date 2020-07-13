package com.rtt.collector.collectorpoc.bot.service;

import com.rtt.collector.collectorpoc.base.BaseService;
import com.rtt.collector.collectorpoc.bot.data.BotRepository;
import com.rtt.collector.collectorpoc.bot.model.Bot;
import com.rtt.collector.collectorpoc.bot.model.BotEntity;
import com.rtt.collector.collectorpoc.bot.model.BotMapper;
import com.rtt.collector.collectorpoc.client.bothub.BotHubClient;
import com.rtt.collector.collectorpoc.exception.BotNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ComboBotService extends BaseService implements BotService {

    private final BotHubClient botHubClient;
    private final BotRepository botRepository;
    private final BotMapper botMapper;

    public ComboBotService(
            BotHubClient botHubClient,
            BotRepository botRepository,
            BotMapper botMapper
    ) {
        this.botHubClient = botHubClient;
        this.botRepository = botRepository;
        this.botMapper = botMapper;
    }

    @Override
    public Bot getBotById(long botId) {
        log.info("{}- Requesting bot #{} ...", Thread.currentThread().getName(), botId);
        Optional<BotEntity> botEntityOptional = botRepository.findById(botId);
        if (botEntityOptional.isPresent()) {
            BotEntity botEntity = botEntityOptional.get();
            return botMapper.toDto(botEntity);
        } else {
            throw new BotNotFoundException(botId);
        }
    }

    @Override
    public boolean validateBot(Bot bot) {
        log.info(
                "{}- Validating bot #{} and bot hub id {}...",
                Thread.currentThread().getName(),
                bot.getId(),
                bot.getBotHubId()
        );
        boolean isValidBot = botHubClient.validateBot(bot.getBotHubId());
        log.info(
                "{}- Bot #{} validated from Combo with status = {}",
                Thread.currentThread().getName(),
                bot.getId(),
                isValidBot ? "ACTIVATED" : "DEACTIVATED"
        );
        return isValidBot;
    }
}
