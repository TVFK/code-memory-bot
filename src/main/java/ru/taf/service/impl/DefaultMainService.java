package ru.taf.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.taf.entity.MemoryPage;
import ru.taf.entity.TgUser;
import ru.taf.enums.BotCommands;
import ru.taf.repository.MemoryPageRepository;
import ru.taf.repository.TgUserRepository;
import ru.taf.service.MainService;
import ru.taf.service.TelegramMemoryBot;
import ru.taf.utils.MessageUtils;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class DefaultMainService implements MainService {

    private final TgUserRepository tgUserRepository;

    private final MemoryPageRepository memoryPageRepository;

    private final MessageUtils messageUtils;

    @Override
    public void processMessage(Update update, TelegramMemoryBot memoryBot) {

        TgUser tgUser = findOrSaveTgUser(update);
        String text = update.getMessage().getText();

        BotCommands cmd = BotCommands.fromValue(text);
        String output;

        if (cmd == null) {
            output = "неверная команда для просмотра команда введите /help";
        } else {
            output = switch (cmd) {
                case HELP -> help();
                case START -> start();
                case ALL_MEMORY_PAGES -> listOfMemoryPages(tgUser, update);
                case NEW_MEMORY_PAGE -> newMemoryPage(tgUser, update);
            };
        }

        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, output);
        memoryBot.sendAnswerMessage(sendMessage);
    }

    private String start() {
        return """
                Страница Памяти – персональная страница усопшего, на которой находятся: его биография, фотографии и памятный фильм\
                Это уникальная страница в сети, на которой можно узнать о жизни человека, который уже давно находится в ином мире.
                Вы можете создавать страницу для своих близких, это лучший современный способ сохранить воспоминания о них.
                Данный бот служит для упрощения созданий таких страниц для сервиса https://memorycode.ru/ благодаря
                возможностям искуственного интеллека, который будет помогать вам заполнять более сложные поля.
                Для просмотра всех команд введите /help
                """;
    }

    private String newMemoryPage(TgUser tgUser, Update update) {
        return "Данная команда пока не доступна";
    }

    private String listOfMemoryPages(TgUser tgUser, Update update) {
         List<MemoryPage> memoryPages = memoryPageRepository.findMemoryPagesByAuthor_Id(tgUser.getId());

         if(memoryPages.isEmpty()){
             return memoryPages.toString();
         } else {
             return "Вы пока не создали ни одной страницы памяти";
         }
    }

    private String help() {
        return """
                Список доступных команд:
                /start - описание бота
                /help - все команды с описанием
                /my_pages - просмотр всех созданных вами страниц памяти
                /new_page - создать новую страницу памяти
                """;
    }

    private TgUser findOrSaveTgUser(Update update) {
        User telegramUser = update.getMessage().getFrom();

        TgUser persistentTgUser = tgUserRepository.findTgUserByTelegramUserId(telegramUser.getId());
        if (persistentTgUser == null) {
            TgUser transientTgUser = TgUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .firstLoginDate(LocalDateTime.now())
                    .build();

            return tgUserRepository.save(transientTgUser);
        }
        return persistentTgUser;
    }
}
