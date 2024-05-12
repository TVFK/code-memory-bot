package ru.taf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.taf.service.TelegramMemoryBot;

@Configuration
public class BotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramMemoryBot memoryBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(memoryBot);
        return api;
    }
}
