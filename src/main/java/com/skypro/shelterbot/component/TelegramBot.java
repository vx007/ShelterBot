package com.skypro.shelterbot.component;

import com.skypro.shelterbot.config.BotConfig;
import com.skypro.shelterbot.exception.EntryAlreadyExists;
import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.service.ReportService;
import com.skypro.shelterbot.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.skypro.shelterbot.resource.StringConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
    private final BotConfig botConfig;
    private final ReportService reportService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        ArrayList<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(START_CMD, START_CAPTION));
        listOfCommands.add(new BotCommand(HELP_CMD, HELP_CAPTION));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    /**
     * основной метод telegramBot, Update получает сообщение от пользователя боту,
     * также хранит всю информацию о нем.
     * <br>
     * включает <b>switch</b> с кнопками (содержат методы)
     * @param update не может быть <b>null</b>, Update received
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            var chatId = message.getChatId();
            var firstName = message.getChat().getFirstName();
            var lastCmd = readUserByChatId(chatId).getLastCommand();

            if (lastCmd != null) {

                switch (lastCmd) {
                    case SEND_REPORT:
                        if (handleReport(message)) {
                            updateUserLastCmd(chatId, null);
                        }
                        break;

                    case LEAVE_YOUR_CONTACT_DETAILS:
                        if (handlePhone(message)) {
                            updateUserLastCmd(chatId, null);
                            sendText(chatId, "Успешно!");
                        } else {
                            sendText(chatId, "Вы ввели неверный номер телефона!");
                        }
                        break;

                    default:
                        sendText(chatId, "Oops!"); //TODO
                }
            } else if (message.hasText()) {
                var text = message.getText();

                switch (text) {
                    case START_CMD:
                        startCommandReceived(chatId, firstName);
                        registerUser(chatId, firstName);
                        universalMenu(chatId, "Выберите приют",
                                SHELTER_CAT,
                                SHELTER_DOG);
                        break;

                    case HELP_CMD:
                        sendText(chatId, HELP_TEXT);
                        break;

                    // Этап 0
                    case SHELTER_CAT:
                        universalMenu(chatId, "Вы выбрали: " + SHELTER_CAT + EMOJI_CAT,
                                INFO_ABOUT_SHELTER_CAT,
                                HOW_TAKE_ANIMAL_FROM_SHELTER_CAT,
                                SEND_REPORT_ABOUT_PET_CAT,
                                CALL_VOLUNTEER);
                        break;

                    case SHELTER_DOG:
                        universalMenu(chatId, "Вы выбрали: " + SHELTER_DOG + EMOJI_DOG,
                                INFO_ABOUT_SHELTER_DOG,
                                HOW_TAKE_ANIMAL_FROM_SHELTER_DOG,
                                SEND_REPORT_ABOUT_PET_DOG,
                                CALL_VOLUNTEER);
                        break;

                    // Этап 1
                    case INFO_ABOUT_SHELTER_CAT:
                        universalMenu(chatId, "Выбрано меню: " + INFO_ABOUT_SHELTER_CAT,
                                TELL_ABOUT_SHELTER_CAT,
                                SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT,
                                GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR_SHELTER_CAT,
                                ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER,
                                LEAVE_YOUR_CONTACT_DETAILS,
                                CALL_VOLUNTEER,
                                BACK_MENU_CAT);
                        break;

                    case INFO_ABOUT_SHELTER_DOG:
                        universalMenu(chatId, "Выбрано меню: " + INFO_ABOUT_SHELTER_DOG,
                                TELL_ABOUT_SHELTER_DOG,
                                SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_DOG,
                                GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR_SHELTER_DOG,
                                ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER,
                                GIVE_A_ADVICE_CYNOLOGIST_A_DOG,
                                ISSUE_RECOMMENDATIONS_ON_PROVEN_CYNOLOGISTS,
                                LEAVE_YOUR_CONTACT_DETAILS,
                                CALL_VOLUNTEER,
                                BACK_MENU_DOG);
                        break;

                    case HOW_TAKE_ANIMAL_FROM_SHELTER_CAT:
                        universalMenu(chatId, "Выбрано меню: " + HOW_TAKE_ANIMAL_FROM_SHELTER_CAT,
                                GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS,
                                GIVE_A_LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER,
                                PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS_CAT,
                                GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN,
                                GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL,
                                GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY,
                                GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE,
                                LEAVE_YOUR_CONTACT_DETAILS,
                                CALL_VOLUNTEER,
                                BACK_MENU_CAT);
                        break;

                    case HOW_TAKE_ANIMAL_FROM_SHELTER_DOG:
                        universalMenu(chatId, "Выбрано меню: " + HOW_TAKE_ANIMAL_FROM_SHELTER_CAT,
                                GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS,
                                GIVE_A_LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER,
                                PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS_DOG,
                                GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_PUPPY,
                                GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL,
                                GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY,
                                GIVE_A_ADVICE_CYNOLOGIST_A_DOG,
                                ISSUE_RECOMMENDATIONS_ON_PROVEN_CYNOLOGISTS,
                                GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE,
                                LEAVE_YOUR_CONTACT_DETAILS,
                                CALL_VOLUNTEER,
                                BACK_MENU_DOG);
                        break;

                    case SEND_REPORT_ABOUT_PET_CAT:
                        universalMenu(chatId, "Выбрано меню: " + SEND_REPORT_ABOUT_PET_CAT,
                                GET_DAILY_REPORT_FROM,
                                SEND_REPORT,
                                CALL_VOLUNTEER,
                                BACK_MENU_CAT);
                        break;

                    case SEND_REPORT_ABOUT_PET_DOG:
                        universalMenu(chatId, "Выбрано меню: " + SEND_REPORT_ABOUT_PET_DOG,
                                GET_DAILY_REPORT_FROM,
                                SEND_REPORT,
                                CALL_VOLUNTEER,
                                BACK_MENU_DOG);
                        break;

                    case BACK_MENU_CAT:
                        universalMenu(chatId, EmojiParser.parseToUnicode("Вы выбрали предыдущее меню: " + SHELTER_CAT + EMOJI_CAT),
                                INFO_ABOUT_SHELTER_CAT,
                                HOW_TAKE_ANIMAL_FROM_SHELTER_CAT,
                                SEND_REPORT_ABOUT_PET_CAT,
                                CALL_VOLUNTEER);
                        break;

                    case BACK_MENU_DOG:
                        universalMenu(chatId, EmojiParser.parseToUnicode("Вы выбрали предыдущее меню: " + SHELTER_DOG + EMOJI_DOG),
                                INFO_ABOUT_SHELTER_DOG,
                                HOW_TAKE_ANIMAL_FROM_SHELTER_DOG,
                                SEND_REPORT_ABOUT_PET_DOG,
                                CALL_VOLUNTEER);
                        break;

                    case CALL_VOLUNTEER:
                        callVolunteer(chatId);
                        break;

                    // Этап 2
                    case TELL_ABOUT_SHELTER_CAT:
                        sendText(chatId, ABOUT_SHELTER_CAT);
                        break;

                    case TELL_ABOUT_SHELTER_DOG:
                        sendText(chatId, ABOUT_SHELTER_DOG);
                        break;

                    case SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT:
                        sendText(chatId, ADDRESS_DIRECTION_ABOUT_SHELTER_CAT);
                        break;

                    case SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_DOG:
                        sendText(chatId, ADDRESS_DIRECTION_ABOUT_SHELTER_DOG);
                        break;

                    case GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR_SHELTER_CAT:
                        sendText(chatId, CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR_SHELTER_CAT);
                        break;

                    case GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR_SHELTER_DOG:
                        sendText(chatId, CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR_SHELTER_DOG);
                        break;

                    case ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER:
                        sendText(chatId, GENERAL_SAFETY_ADVICE_AT_THE_SHELTER);
                        break;

                    case LEAVE_YOUR_CONTACT_DETAILS:
                        sendText(chatId, firstName + ", оставьте свой номер телефона в формате XXXXXXXXXXX!");
                        updateUserLastCmd(chatId, LEAVE_YOUR_CONTACT_DETAILS);
                        break;

                    // Этап 3
                    case GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS:
                        sendText(chatId, RULES_OF_ACQUAINTANCE_WITH_ANIMALS);
                        break;

                    case GIVE_A_LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER:
                        sendText(chatId, LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER);
                        break;

                    case PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS_CAT:
                        sendText(chatId, LIST_OF_TRANSPORTATION_RECOMMENDATIONS_CAT);
                        break;

                    case PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS_DOG:
                        sendText(chatId, LIST_OF_TRANSPORTATION_RECOMMENDATIONS_DOG);
                        break;

                    case GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN:
                        sendText(chatId, LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN);
                        break;

                    case GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_PUPPY:
                        sendText(chatId, LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_PUPPY);
                        break;

                    case GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL:
                        sendText(chatId, LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL);
                        break;

                    case GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY:
                        sendText(chatId, LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY);
                        break;

                    case GIVE_A_ADVICE_CYNOLOGIST_A_DOG:
                        sendText(chatId, LIST_WHAT_NEEDED_KNOWS_ABOUT_DOG);
                        break;

                    case ISSUE_RECOMMENDATIONS_ON_PROVEN_CYNOLOGISTS:
                        sendText(chatId, LIST_RECOMMENDED_CENTRE_CYNOLOGISTS);
                        break;

                    case GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE:
                        sendText(chatId, LIST_OF_REASONS_WHY_THEY_CAN_REFUSE);
                        break;

                    // Этап 4
                    case GET_DAILY_REPORT_FROM:
                        sendText(chatId, DAILY_REPORT_FROM);
                        break;

                    case SEND_REPORT:
                        sendText(chatId, "Присылайте отчёт!");
                        updateUserLastCmd(chatId, SEND_REPORT);
                        break;

                    default:
                        sendText(chatId, "Извините, команда не распознана!");
                }
            }
        }
    }

    /**
     * приветствуем пользователя
     * @param chatId чат ид пользователя
     * @param name имя пользователя
     */
    private void startCommandReceived(Long chatId, String name) {
        var text = EmojiParser.parseToUnicode("Добро пожаловать!:blush:");
        sendText(chatId, text);
        log.info("Заходил пользователь\\ ЧАТИД: {}, ИМЯ: {}", chatId, name);
    }

    /**
     * регистрируем пользователя, добавляем в таблицу ид и имя
     * @param chatId чат ид пользователя
     * @param name имя пользователя
     */
    private void registerUser(Long chatId, String name) {
        var user = new User();
        user.setChatId(chatId);
        user.setName(name);
        user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));
        createUser(user);
    }

    /**
     * отправляем сообщение пользователю
     * @param chatId чат ид пользователя
     * @param text текст пользователю
     */
    private void sendText(Long chatId, String text) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeSendMessage(message);
    }

    /**
     * через <b>execute</b> - встроенный метод telegramBot, отправляем текс пользователю, отлавиваем ошибку
     * @param message не может быть <b>null</b> содержит данные о текущем пользователе
     */
    private void executeSendMessage(SendMessage message) {
        try {
            log.info("Отправка сообщения\\ ЧАТИД: {}, ТЕКСТ: {}", message.getChatId(), message.getText());
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    /**
     * вызов волантера, включает в себя кнопки выбора
     * @param chatId чат ид пользователя
     */
    private void callVolunteer(Long chatId) {
        sendText(chatId, TEXT_OF_VOLUNTEER);//TODO
        var text = "Выберите из списка меню: ";
        universalMenu(chatId, text,
                LEAVE_YOUR_CONTACT_DETAILS,
                "Перезвоните мне",
                START_CMD,
                HELP_CMD);
    }

    /**
     * метод присваивания кнопок в telegramBot
     * @param chatId получает ид пользователя
     * @param text содержит входящий текст
     * @param stringsForRows в параметр варарга, через запятую,
     * может содержать не ограниченое колличество строк(они же будут названием кнопки).
     * KeyboardRow - создает кнопку
     * ArrayList включает в себя все принятые кнопки
     */
    private void universalMenu(Long chatId, String text, String... stringsForRows) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        for (var string : stringsForRows) {
            var row = new KeyboardRow();
            row.add(string);
            keyboardRows.add(row);
        }

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        executeSendMessage(message);
    }

    /**
     * если содержит в запросе от пользователя текст из switch, совпадающий с параметром, выполнит метод принятия отчета от пользователя,
     * добавит данные в таблицу reports
     * @param message не может быть <b>null</b>
     * @return true если выполнилось верно и false если не верно
     */
    private boolean handleReport(Message message) {
        var chatId = message.getChat().getId();
        var user = readUserByChatId(chatId);
        var photoId = message.getPhoto().get(message.getPhoto().size() - 1).getFileUniqueId();
        var text = message.getCaption();

        var report = new Report(user, photoId, text, Timestamp.valueOf(LocalDateTime.now()), false);
        reportService.add(report);
        return true;
    }

    /**
     * если содержит в запросе от пользователя текст из switch, совпадающий с параметром, выполнит метод записи телефона пользователя,
     * добавит данные в таблицу users
     * @param message не может быть <b>null</b>
     * @return true если выполнилось верно и false если не верно
     */
    private boolean handlePhone(Message message) {
        var chatId = message.getFrom().getId();
        if (message.hasText()) {
            var text = message.getText();
            var matcher = PHONE_PATTERN.matcher(text);
            if (matcher.matches()) {
                updateUserPhone(chatId, text);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void createUser(User user) {
        try {
            log.info("Попытка сохранения пользователя\\ {}", user);
            userService.add(user);
        } catch (EntryAlreadyExists e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private User readUserByChatId(Long chatId) {
        try {
            log.info("Попытка найти пользователя\\ ЧАТИД: {}", chatId);
            return userService.getByChatId(chatId);
        } catch (EntryNotFoundException e) {
            log.error(ERROR_TEXT + e.getMessage());
            return new User();
        }
    }

    private void updateUserLastCmd(Long chatId, String cmd) {
        try {
            log.info("Попытка обновить пользователя\\ ЧАТИД: {}, КОММАНДА: {}", chatId, cmd);
            userService.updateLastCommand(chatId, cmd);
        } catch (EntryNotFoundException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void updateUserPhone(Long chatId, String phone) {
        try {
            log.info("Попытка обновить пользователя\\ ЧАТИД: {}, ТЕЛЕФОН: {}", chatId, phone);
            userService.updatePhone(chatId, phone);
        } catch (EntryNotFoundException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
}
