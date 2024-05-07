package ru.taf.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.taf.config.TelegramMemoryBot;

public interface UpdateService {
    void processMessage(Update update);

    void registerBot(TelegramMemoryBot memoryBot);
}
