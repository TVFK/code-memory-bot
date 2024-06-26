package ru.taf.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.taf.service.impl.DefaultMainService;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class TelegramMemoryBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    private final MainService mainService;

    public TelegramMemoryBot(@Value("${bot.token}") String botToken, @Lazy MainService mainService) {
        super(botToken);
        this.mainService = mainService;

        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "Приветсвенное сообщение"));
        listOfCommand.add(new BotCommand("/help", "Список команд"));
        listOfCommand.add(new BotCommand("/my_pages", "Список всех страниц памяти"));
        listOfCommand.add(new BotCommand("/new_page", "Создать новую страницу памяти"));

        try {
            execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error set bot command list: " + e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        mainService.processMessage(update);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public void sendAnswerMessage(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("start");
        row.add("help");

        rows.add(row);

        replyKeyboardMarkup.setKeyboard(rows);

        if (sendMessage != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error send message: " + e);
            }
        }
    }
}
