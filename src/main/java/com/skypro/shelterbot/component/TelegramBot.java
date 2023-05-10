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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final ReportService reportService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(StringConstants.START_CMD, StringConstants.START_CAPTION));
        listOfCommands.add(new BotCommand(StringConstants.HELP_CMD, StringConstants.HELP_CAPTION));
        listOfCommands.add(new BotCommand(StringConstants.SETTINGS_CMD, StringConstants.SETTINGS_CAPTION));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(StringConstants.ERROR_TEXT + e.getMessage());
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
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            var message = update.getMessage();
            var chat = message.getChat();
            var chatId = chat.getId();
            var user = userService.getByChatId(chatId);

            if (user.getLastCommand().equals(StringConstants.SEND_REPORT)) {
                if (handleReport(message)) {
                    userService.updateLastCommand(chatId, null);
                }
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            var messageText = message.getText();
            var chat = message.getChat();
            var chatId = chat.getId();
            var firstName = chat.getFirstName();

            if (messageText.startsWith("/send") && botConfig.getOwnerId().equals(chatId)) {
                var text = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                for (var user : userService.getAll()) {
                    sendText(user.getChatId(), text);
                }
            } else {
                switch (messageText) {
                    case StringConstants.START_CMD:
                        startCommandReceived(chatId, firstName);
                        registerUser(chatId, firstName);
                        offerCatOrDog(chatId);
                        break;
                    case StringConstants.HELP_CMD:
                        sendText(chatId, StringConstants.HELP_TEXT);
                        break;
                    case StringConstants.SETTINGS_CMD:
                        sendText(chatId, "no text");
                        break;

                    // Этап 1
                    case StringConstants.INFO_ABOUT_SHELTER_CAT:
                        universalMenu(chatId, "Выбрано меню",
                                StringConstants.TELL_ABOUT_SHELTER_CAT,
                                StringConstants.SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT,
                                StringConstants.GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR,
                                StringConstants.ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER,
                                StringConstants.LEAVE_YOUR_CONTACT_DETAILS,
                                StringConstants.CALL_VOLUNTEER);
                        break;

                    case StringConstants.HOW_TAKE_ANIMAL_FROM_SHELTER:
                        universalMenu(chatId, "Выбрано меню",
                                StringConstants.GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS,
                                StringConstants.GIVE_A_LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER,
                                StringConstants.PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS,
                                StringConstants.GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN,
                                StringConstants.GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL,
                                StringConstants.GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY,
                                StringConstants.GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE,
                                StringConstants.LEAVE_YOUR_CONTACT_DETAILS,
                                StringConstants.CALL_VOLUNTEER);
                        break;

                    case StringConstants.SEND_REPORT_ABOUT_PET:
                        universalMenu(chatId, "Выбрано меню",
                                StringConstants.GET_DAILY_REPORT_FROM,
                                StringConstants.SEND_REPORT,
                                StringConstants.CALL_VOLUNTEER);
                        break;

                    case StringConstants.CALL_VOLUNTEER:
                        callVolunteer(chatId);
                        break;

                    // Этап 2
                    case StringConstants.TELL_ABOUT_SHELTER_CAT:
                        sendText(chatId, StringConstants.ABOUT_SHELTER_CAT);
                        break;

                    case StringConstants.SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT:
                        sendText(chatId, StringConstants.ADDRESS_DIRECTION_ABOUT_SHELTER_CAT);

                        break;

                    case StringConstants.GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR:
                        sendText(chatId, StringConstants.CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR);
                        break;

                    case StringConstants.ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER:
                        sendText(chatId, StringConstants.GENERAL_SAFETY_ADVICE_AT_THE_SHELTER);
                        break;

                    case StringConstants.LEAVE_YOUR_CONTACT_DETAILS:
                        sendText(chatId, firstName + ", оставьте свои данные!");
                        break;

                    // Этап 3
                    case StringConstants.GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS:
                        sendText(chatId, StringConstants.RULES_OF_ACQUAINTANCE_WITH_ANIMALS);
                        break;

                    case StringConstants.GIVE_A_LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER:
                        sendText(chatId, StringConstants.LIST_OF_DOCUMENTS_TO_ADOPT_AN_ANIMAL_FROM_A_SHELTER);
                        break;

                    case StringConstants.PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS:
                        sendText(chatId, StringConstants.LIST_OF_TRANSPORTATION_RECOMMENDATIONS);
                        break;

                    case StringConstants.GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN:
                        sendText(chatId, StringConstants.LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN);
                        break;

                    case StringConstants.GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL:
                        sendText(chatId, StringConstants.LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL);
                        break;

                    case StringConstants.GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY:
                        sendText(chatId, StringConstants.LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY);
                        break;

                    case StringConstants.GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE:
                        sendText(chatId, StringConstants.LIST_OF_REASONS_WHY_THEY_CAN_REFUSE);
                        break;

                    // Этап 4
                    case StringConstants.GET_DAILY_REPORT_FROM:
                        sendText(chatId, StringConstants.DAILY_REPORT_FROM);
                        break;

                    case StringConstants.SEND_REPORT:
                        sendText(chatId, "Присылайте отчёт!");
                        userService.updateLastCommand(chatId, StringConstants.SEND_REPORT);
                        break;
                    default:
                        sendText(chatId, "Извините, команда не распознана!");
                }
            }

        } else if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var message = update.getCallbackQuery().getMessage();
            var chatId = message.getChatId();

            switch (data) {
                case StringConstants.CAT_BUTTON:
                    var catButtonText = EmojiParser.parseToUnicode("Вы выбрали приют кошек!:cat:");
                    universalMenu(chatId, catButtonText,
                            StringConstants.INFO_ABOUT_SHELTER_CAT,
                            StringConstants.HOW_TAKE_ANIMAL_FROM_SHELTER,
                            StringConstants.SEND_REPORT_ABOUT_PET,
                            StringConstants.CALL_VOLUNTEER);
                    break;
                case StringConstants.DOG_BUTTON:
                    var dogButtonText = EmojiParser.parseToUnicode("Вы выбрали приют собак!:dog:");
                    sendText(chatId, dogButtonText); //TODO
                    break;
                default:
                    sendText(chatId, "Извините, команда не распознана!");
                    break;
            }
        }
    }

    private void offerCatOrDog(Long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите приют");

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> listButtonFirst = new ArrayList<>();

        var catButton = new InlineKeyboardButton();
        catButton.setText(EmojiParser.parseToUnicode("Приют кошек:cat:"));
        catButton.setCallbackData(StringConstants.CAT_BUTTON);

        var dogButton = new InlineKeyboardButton();
        dogButton.setText(EmojiParser.parseToUnicode("Приют собак:dog:"));
        dogButton.setCallbackData(StringConstants.DOG_BUTTON);

        listButtonFirst.add(catButton);
        listButtonFirst.add(dogButton);

        listOfButtons.add(listButtonFirst);
        inlineKeyboardMarkup.setKeyboard(listOfButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeSendMessage(message);
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
        sendText(chatId, StringConstants.TEXT_OF_VOLUNTEER); //TODO
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

        var report = new Report(user, photoId, text, LocalDateTime.now(), false);
        reportService.add(report);
        return true;
    }
}
