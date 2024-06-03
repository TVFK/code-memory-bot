package ru.taf.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.taf.entity.Children;
import ru.taf.entity.MemoryPage;
import ru.taf.entity.Person;
import ru.taf.entity.TgUser;
import ru.taf.enums.BotCommand;
import ru.taf.enums.UserState;
import ru.taf.repository.ChildrenRepository;
import ru.taf.repository.MemoryPageRepository;
import ru.taf.repository.PersonRepository;
import ru.taf.repository.TgUserRepository;
import ru.taf.service.MainService;
import ru.taf.service.TelegramMemoryBot;
import ru.taf.utils.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DefaultMainService implements MainService {

    private final TgUserRepository tgUserRepository;

    private final MemoryPageRepository memoryPageRepository;

    private final MessageUtils messageUtils;

    private final TelegramMemoryBot memoryBot;

    private final PersonRepository personRepository;

    private final ChildrenRepository childrenRepository;

    @Override
    public void processMessage(Update update) {

        TgUser tgUser = findOrSaveTgUser(update);
        String output = "";

        if (update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
            output = processCommand(update, tgUser);
        } else {
            if (tgUser.getUserState() != UserState.NONE) {
                output = processUserState(tgUser, update);
            } else {
                output = "Неверная команда! Для просмотра всех команд введите /help";
            }
        }

        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, output);
        memoryBot.sendAnswerMessage(sendMessage);
    }

    private String processCommand(Update update, TgUser tgUser) {
        String text = update.getMessage().getText();
        BotCommand cmd = BotCommand.fromValue(text);
        if (cmd == null) {
            return "Неверная команда! Для просмотра всех команд введите /help";
        } else {
            return switch (cmd) {
                case HELP -> help();
                case START -> start();
                case ALL_MEMORY_PAGES -> listOfMemoryPages(tgUser, update);
                case NEW_MEMORY_PAGE -> newMemoryPage(tgUser, update);
            };
        }
    }

    private String newMemoryPage(TgUser tgUser, Update update) {
        if (tgUser.getUserState() != UserState.NONE) {
            return "Для начала завершите создание текущей страницы памяти";
        }

        Person person = new Person();
        tgUser.setTempData(person);
        tgUser.setUserState(UserState.PHOTO);
        tgUserRepository.save(tgUser);

        return """
                Вы выбрали команду для создания новой страницы памяти!
                Вам будет последовательно задаваться ряд вопросов, всего их будет 14,
                 с заполнением некоторых полей вам поможет нейросеть!
                После этого из ваших ответов будет сформированна страница памяти, а теперь приступим!
                                
                1) Отправьте фотографию человека
                """;
    }

    private String listOfMemoryPages(TgUser tgUser, Update update) {
        Optional<List<MemoryPage>> memoryPages = memoryPageRepository.findMemoryPagesByAuthor_Id(tgUser.getId());

        if (memoryPages.isEmpty()) {
            return memoryPages.toString();
        } else {
            return "Вы пока не создали ни одной страницы памяти";
        }
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
                    .userState(UserState.NONE)
                    .build();

            return tgUserRepository.save(transientTgUser);
        }
        return persistentTgUser;
    }

    private String processUserState(TgUser tgUser, Update update) {
        Message message = update.getMessage();

        Person currentPerson = tgUser.getTempData();

        String answer = "Что-то пошло не так...";

        switch (tgUser.getUserState()) {
            case PHOTO -> {
                if (message.hasPhoto()) {
                    PhotoSize photoInf = message.getPhoto().get(0);
                    GetFile getFile = new GetFile(photoInf.getFileId());

                    try {
                        File file = memoryBot.execute(getFile);
                        InputStream photoStream = memoryBot.downloadFileAsStream(file);
                        currentPerson.setPhoto(photoStream.readAllBytes());
                        photoStream.close();
                    } catch (TelegramApiException e) {
                        log.error("Ошибка при скачивании фото: " + e);
                    } catch (IOException e) {
                        log.error("Ошибка при сохраненнии фото в объект person" + e);
                    }
                } else {
                    return "Данные неккоректные, отправьте фотографию!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.FULL_NAME);
                tgUserRepository.save(tgUser);
                answer = "Фото успешно сохранено! Теперь введите полное имя человека";
            }
            case FULL_NAME -> {
                if (message.hasText()) {
                    currentPerson.setFullName(message.getText());
                } else {
                    return "Данные неккоректны! отправьте полное имя человека!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.BIRTH_DATE);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите дату рождения человека в формате гггг-мм-дд (пример: 1960-01-01)";
            }
            case BIRTH_DATE -> {
                if (message.hasText()) {
                    String text = message.getText();
                    LocalDate birthDate = LocalDate.parse(text);
                    currentPerson.setBirthDate(birthDate);
                } else {
                    return "Данные неккоректны! отправьте дату рождения!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.DEATH_DATE);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите дату смерти в формате гггг-мм-дд (пример: 1960-01-01)";
            }
            case DEATH_DATE -> {
                if (message.hasText()) {
                    String text = message.getText();
                    LocalDate deathDate = LocalDate.parse(text);
                    currentPerson.setDeathDate(deathDate);
                } else {
                    return "Данные неккоректны! отправьте дату смерти!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.BIRTH_PLACE);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь напишите место рождения человека";
            }
            case BIRTH_PLACE -> {
                if (message.hasText()) {
                    currentPerson.setBirthPlace(message.getText());
                } else {
                    return "Данные неккоректны! отправьте место рождения человека!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.CHILDREN);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь через запятую введите имена детей даннного человека," +
                        "если детей нет, то отправьте -";
            }
            case CHILDREN -> {
                if (message.hasText()) {
                    String text = message.getText();
                    List<Children> children = new ArrayList<>();
                    if (text.equals("-")) {
                        return "Данные успешно сохранены! Теперь введите гражданство человека";
                    } else {
                        String[] childrenArray = text.split(", ");
                        for (String s : childrenArray) {
                            Children child = new Children();
                            child.setName(s);
                            child.setParent(currentPerson);
                            children.add(child);
                        }
                        currentPerson.setChildren(children);
                    }
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.CITIZENSHIP);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите гражданство человека";
            }
            case CITIZENSHIP -> {
                if (message.hasText()) {
                    currentPerson.setCitizenship(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.SPOUSE);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите имя супруга человека";
            }
            case SPOUSE -> {
                if (message.hasText()) {
                    currentPerson.setSpouse(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.OCCUPATION);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите профессию человека";
            }
            case OCCUPATION -> {
                if (message.hasText()) {
                    currentPerson.setOccupation(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.EDUCATION);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите образование человека";
            }
            case EDUCATION -> {
                if (message.hasText()) {
                    currentPerson.setEducation(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.AWARDS);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите награды человека";
            }
            case AWARDS -> {
                if (message.hasText()) {
                    currentPerson.setAwards(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.BURIAL_PLACE);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь введите место захоронения человека";
            }
            case BURIAL_PLACE -> {
                if (message.hasText()) {
                    currentPerson.setBurialPlace(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setTempData(currentPerson);
                tgUser.setUserState(UserState.EPITAPH);
                tgUserRepository.save(tgUser);
                answer = "Данные успешно сохранены! Теперь напишите эпитафию";
            }
            case EPITAPH -> {
                if (message.hasText()) {
                    currentPerson.setEpitaph(message.getText());
                } else {
                    return "Данные неккоректны!";
                }
                tgUser.setUserState(UserState.NONE);
                MemoryPage memoryPage = new MemoryPage();
                memoryPage.setAuthor(tgUser);
                memoryPage.setPerson(currentPerson);
                currentPerson.setMemoryPage(memoryPage);

                personRepository.save(currentPerson);
                childrenRepository.saveAll(currentPerson.getChildren());
                memoryPageRepository.save(memoryPage);
                answer = "Отлично! Вы создали страницу памяти, чтобы просмотреть все свои страницы памяти введите /mypage";
            }
            default -> throw new IllegalStateException("Unexpected value: " + tgUser.getUserState());
        }
        return answer;
    }
}
