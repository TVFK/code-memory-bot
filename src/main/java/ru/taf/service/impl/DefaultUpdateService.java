package ru.taf.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.taf.config.TelegramMemoryBot;
import ru.taf.service.UpdateService;
import ru.taf.utils.MessageUtils;

@Component
@Log4j2
@RequiredArgsConstructor
public class DefaultUpdateService implements UpdateService {

    private final MessageUtils messageUtils;

    private TelegramMemoryBot memoryBot;

    @Override
    public void registerBot(TelegramMemoryBot memoryBot) {
        this.memoryBot = memoryBot;
    }

    @Override
    public void processMessage(Update update) {
        if(update == null){
            log.error("Received message is null");
            return;
        } else if(update.getMessage() != null){
            processMessageByType(update);
        } else {
            log.error("Invalid message type" + update);
        }
    }

    private void processMessageByType(Update update) {
        Message message = update.getMessage();


        if(message.hasText()){
            processText(update);
        } else if(message.hasDocument()){
            processDocument(update);
        } else if(message.hasPhoto()){
            processPhoto(update);
        } else {
            SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,
                    "Данный тип сообщений не поддерживается :(");
            memoryBot.sendAnswerMessage(sendMessage);
        }
    }

    private void processPhoto(Update update) {
        memoryBot.sendAnswerMessage(messageUtils.generateSendMessageWithText(update, "PHOTO"));
    }

    private void processDocument(Update update) {
        memoryBot.sendAnswerMessage(messageUtils.generateSendMessageWithText(update, "DOCUMENT"));
    }

    private void processText(Update update) {
        memoryBot.sendAnswerMessage(messageUtils.generateSendMessageWithText(update, "TEXT"));
    }
}
