package com.rtt.collector.collectorpoc.bot.usecase;

import com.rtt.collector.collectorpoc.annotation.UseCase;
import com.rtt.collector.collectorpoc.base.BaseUseCase;
import com.rtt.collector.collectorpoc.bot.model.Bot;
import com.rtt.collector.collectorpoc.bot.service.BotService;
import lombok.Setter;

@UseCase
public class GetBotUseCase extends BaseUseCase<Bot, GetBotUseCase.Parameters> {

    private final BotService botService;

    public GetBotUseCase(BotService botService) {
        this.botService = botService;
    }

    @Override
    public Bot execute(Parameters parameters) {
        return botService.getBotById(parameters.botId);
    }

    @Setter
    public static class Parameters implements BaseUseCase.UseCaseParameters {
        private long botId;

        private Parameters() {}

        public static Parameters build(long id) {
            return new Parameters() {{
                setBotId(id);
            }};
        }
    }
}
