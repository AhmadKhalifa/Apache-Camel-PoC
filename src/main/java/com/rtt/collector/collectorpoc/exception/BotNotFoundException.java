package com.rtt.collector.collectorpoc.exception;

public class BotNotFoundException extends ResourceNotFoundException {

    public BotNotFoundException(long botId) {
        super(String.format("Bot %d not found", botId));
    }
}
