package com.rtt.collector.collectorpoc.bot.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.bot.model.Bot;
import com.rtt.collector.collectorpoc.bot.service.BotService;
import lombok.Setter;

@UseCase
public class ValidateBotUseCase
        extends BaseUseCase<Boolean, ValidateBotUseCase.Parameters> {

    private final BotService botService;

    public ValidateBotUseCase(BotService botService) {
        this.botService = botService;
    }

    @Override
    public Boolean execute(Parameters parameters) {
        return botService.validateBot(parameters.bot);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {

        private Bot bot;

        private Parameters() {}

        public static Parameters build(Bot bot) {
            return new Parameters() {{
                setBot(bot);
            }};
        }
    }
}
