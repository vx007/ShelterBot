package com.skypro.shelterbot.component;

import com.skypro.shelterbot.config.BotConfig;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.resource.StringConstants;
import com.skypro.shelterbot.service.ReportService;
import com.skypro.shelterbot.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.skypro.shelterbot.resource.StringConstants.*;

@Log4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
    private final BotConfig botConfig;
    private final ReportService reportService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        List<BotCommand> listOfCommands = new ArrayList<>();
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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            var chatId = message.getFrom().getId();
            var firstName = message.getFrom().getFirstName();

            if (update.getMessage().hasPhoto()) {
                var user = userService.getByChatId(chatId);

                if (user.getLastCommand().equals(SEND_REPORT)) {
                    if (handleReport(message)) {
                        userService.updateLastCommand(chatId, null);
                    }
                }
            } else if (update.getMessage().hasText()) {
                switch (message.getText()) {
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
                        sendText(chatId, firstName + ", оставьте свои данные!");
                        userService.updateLastCommand(chatId, LEAVE_YOUR_CONTACT_DETAILS);

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

                    case GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE:
                        sendText(chatId, LIST_OF_REASONS_WHY_THEY_CAN_REFUSE);
                        break;

                    // Этап 4
                    case GET_DAILY_REPORT_FROM:
                        sendText(chatId, DAILY_REPORT_FROM);
                        break;

                    case SEND_REPORT:
                        sendText(chatId, "Присылайте отчёт!");
                        userService.updateLastCommand(chatId, SEND_REPORT);
                        break;

                    default:
                        sendText(chatId, "Извините, команда не распознана!");
                }
            }
        }
    }


    private void startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, welcome to the Shelter Bot!" + " :blush:");
        log.info("Заходил пользователь: " + name);
        sendText(chatId, answer);
    }

    private void registerUser(Long chatId, String name) {
        var user = new User();
        user.setChatId(chatId);
        user.setName(name);
        user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));
        userService.add(user);
        log.info("User saved: " + user);
    }

    private void sendText(Long chatId, String text) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeSendMessage(message);
    }

    private void executeSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(StringConstants.ERROR_TEXT + e.getMessage());
        }
    }

    private void callVolunteer(Long chatId) {
        sendText(chatId, TEXT_OF_VOLUNTEER);//TODO
        String anyText = "Выберите из списка меню: ";
        universalMenu(chatId, anyText, LEAVE_YOUR_CONTACT_DETAILS, "Перезвоните мне", START_CMD, HELP_CMD);
    }

    private void universalMenu(Long chatId, String text, @NotNull String... stringsForRows) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
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

    private boolean handleReport(Message message) {
        var chatId = message.getChat().getId();
        var user = userService.getByChatId(chatId);
        var photoId = message.getPhoto().get(message.getPhoto().size() - 1).getFileUniqueId();
        var text = message.getCaption();

        var report = new Report(user, photoId, text, Timestamp.valueOf(LocalDateTime.now()), false);
        reportService.add(report);
        return true;
    }

    private boolean handlePhone(Long chatId, String text) {
        var matcher = PHONE_PATTERN.matcher(text);
        if (matcher.matches()) {
            userService.updatePhone(chatId, text);
            return true;
        } else {
            return false;
        }
    }
}
