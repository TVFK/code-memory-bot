package ru.taf.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.taf.service.UpdateService;

@Log4j2
@Component
public class TelegramMemoryBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    private final UpdateService updateService;

    public TelegramMemoryBot(@Value("${bot.token}") String botToken, UpdateService updateService){
        super(botToken);
        this.updateService = updateService;
    }

    @PostConstruct
    public void init(){
        updateService.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateService.processMessage(update);
    }
    @Override
    public String getBotUsername() {
        return botName;
    }

    public void sendAnswerMessage(SendMessage sendMessage){
        if (sendMessage != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
