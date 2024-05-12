package ru.taf.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MessageUtils {
    public SendMessage generateSendMessageWithText(Update update, String messageText){
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(messageText);
        return sendMessage;
    }
}
