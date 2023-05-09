package com.skypro.shelterbot.component;

import com.skypro.shelterbot.config.BotConfig;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.UserRepository;
import com.skypro.shelterbot.resource.StringConstants;
import com.skypro.shelterbot.service.ReportService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.log4j.Log4j;
import org.springframework.cache.CacheManager;
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

/**
 * @extends {@link TelegramLongPollingBot} идет проверка не написали ли что то
 */
@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final ReportService reportService;

    public final CacheManager cacheManager;

    static final String ERROR_TEXT = "Error occurred: ";
    static final String HELP_TEXT = "Этот бот создан, что бы вы смогли найти себе подходящего питомца \n\n" +
            "Попробуйте начать с кнопки .\n\n" +
            "Type /start чтобы увидеть приветсятвеное сообщение.\n\n" +
            "Type /mydata чтобы увидеть инфо о себе.\n\n" +
            "Type /help увидеть это сообщение снова.";
    static final String CAT_BUTTON = "CAT_BUTTON";
    static final String DOG_BUTTON = "DOG_BUTTON";
    static final String INFO_ABOUT_SHELTER_CAT = "Узнать информацию о приюте";
    static final String HOW_TAKE_ANIMAL_FROM_SHELTER = "Как взять животное из приюта";
    static final String SEND_REPORT_ABOUT_PET = "Прислать отчет о питомце";
    static final String CALL_VOLUNTEER = "Позвать волонтера";
    static final String TELL_ABOUT_SHELTER_CAT = "Рассказать о приюте кошек";
    static final String SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT = "Выдать расписание работы приюта и адрес, схему проезда";
    static final String GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR = "Выдать контактные данные охраны для оформления пропуска на машину";
    static final String ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER = "Выдать общие рекомендации о технике безопасности на территории приюта";
    static final String LEAVE_YOUR_CONTACT_DETAILS = "Оставить свои данные для связи";
    static final String GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS = "Выдать правила знакомства с животными";
    static final String GIVE_A_LIST_OF_DOCUMENTS_tO_ADOPt_AN_ANIMAL_FrOM_A_SHELTER = "Выдать список документов, чтобы взять животное из приюта";
    static final String PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS = "Выдать список рекомендаций по транспортировке";
    static final String GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN = "Выдать список рекомендаций по обустройству дома для котёнка";
    static final String GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL = "Выдать список рекомендаций по обустройству дома для взрослого животного";
    static final String GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY = "Выдать список рекомендаций по обустройству дома для животного с ограниченными возможностями";
    static final String GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE = "Выдать список причин, почему могут отказать";
    static final String GET_DAILY_REPORT_FROM = "Получить форму ежедневного отчета";
    static final String TEXT_OF_VOLUNTEER = "Привет я волонтер, чем могу помочь?";

    public TelegramBot(BotConfig botConfig, UserRepository userRepository, ReportService reportService, CacheManager cacheManager) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.cacheManager = cacheManager;
    }

    public void cleanCache() {
        cacheManager.getCache("users").clear();
        
    }

    @PostConstruct
    public void init() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы с Shelter Bot!"));
        listOfCommands.add(new BotCommand("/help", "как использовать бота"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage());
        }
    }

    /**
     * Update класс получает сообщение от пользователя боту,
     * также хвранит всю всю инфо о нем
     *
     * @param update update
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            var messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            var name = update.getMessage().getChat().getFirstName();
            User u = userRepository.findByChatId(chatId).orElse(null);

            if ("Отправить отчёт".equals("")){
                if (handleReport(update.getMessage())) {
                    updateLastCommand(chatId, "");
                }
            } else if (messageText.contains("/send") && botConfig.getOwnerId().equals(chatId)) { //отправка всем пользователям сообщения от проверенного пользователя
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));// метод substring позволяет получить текст после /send
                var users = userRepository.findAll();
                for (var user : users) {
                    sendText(user.getChatId(), textToSend);
                }
            } else {

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, name);
                        registerUser(update.getMessage());
                        offerCatOrDog(chatId);
                        break;

                    case "/volunteer":
                        callVolunteer(chatId);
                        break;
//                       1.

                    case INFO_ABOUT_SHELTER_CAT:
                        menuFindInformationAboutShelterCat(chatId, "Привет!" + name);
                        break;

                    case HOW_TAKE_ANIMAL_FROM_SHELTER:
                        menuHowTakeAnimal(chatId, "Привет!" + name);
                        break;

                    case SEND_REPORT_ABOUT_PET:
                        menuSendReportAboutPet(chatId, "Привет!" + name);
                        break;

                    case CALL_VOLUNTEER:
                        callVolunteer(chatId);
                        break;
//                        2.

                    case TELL_ABOUT_SHELTER_CAT:

                        break;

                    case SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT:
                        break;

                    case GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR:
                        break;

                    case ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER:
                        break;

                    case LEAVE_YOUR_CONTACT_DETAILS:
                        sendText(chatId, name + ", оставьте свои данные!");
                        break;
//                        3.

                    case GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS:
                        break;

                    case GIVE_A_LIST_OF_DOCUMENTS_tO_ADOPt_AN_ANIMAL_FrOM_A_SHELTER:
                        break;

                    case PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS:
                        break;

                    case GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN:
                        break;

                    case GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL:
                        break;

                    case GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY:
                        break;

                    case GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE:
                        break;

//                        4.
                    case GET_DAILY_REPORT_FROM:
                        break;

                    case StringConstants.HELP_TEXT:
                        sendText(chatId, StringConstants.HELP_TEXT);
                        break;

                    case "Отправить отчёт":
                        sendText(chatId, "Присылайте отчёт!");
//                        receiveReport(update.getMessage());
                        updateLastCommand(chatId, "Отправить отчёт");
                        break;
                    default:
                        sendText(chatId, "Извините, команда не распознана!\n\n" + HELP_TEXT);
                }
            }

        } else if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var message = update.getCallbackQuery().getMessage();
//            var messageId = message.getMessageId();
            var chatId = message.getChatId();

            if (data.equals(CAT_BUTTON)) {
                var text = EmojiParser.parseToUnicode("Вы выбрали приют кошек! :cat:");
                mainMenuShelterForCat(chatId, text);

            } else if (data.equals(DOG_BUTTON)) {
                var text = EmojiParser.parseToUnicode("Вы выбрали приют собак! :dog:\n\nУпс эта функция в разработке!");
                sendText(chatId, text);

            }
        }
    }

    private void updateLastCommand(Long chatId, String command) {
        if (userRepository.findByChatId(chatId).isPresent()) {
            var user = userRepository.findByChatId(chatId).get();
            user.setLastCommand(command);
            userRepository.save(user);
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
        catButton.setText(EmojiParser.parseToUnicode("Приют кошек :cat:"));
        catButton.setCallbackData(CAT_BUTTON);

        var dogButton = new InlineKeyboardButton();
        dogButton.setText(EmojiParser.parseToUnicode("Приют собак :dog:"));
        dogButton.setCallbackData(DOG_BUTTON);

        listButtonFirst.add(catButton);
        listButtonFirst.add(dogButton);

        listOfButtons.add(listButtonFirst);
        inlineKeyboardMarkup.setKeyboard(listOfButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeSendMessage(message);
    }

//    private void executeEditMessageText(Long chatId, Integer messageId, String text) { // меотод показывает что нажал
//        EditMessageText message = new EditMessageText();
//        message.setChatId(chatId);
//        message.setMessageId(messageId);
//        message.setText(text);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            log.error(ERROR_TEXT + e.getMessage());
//        }
//    }

    /**
     * welcome message to new user
     *
     * @param chatId can't be <b>null</b>
     * @param name   can't be <b>null</b>
     */
    private void startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, welcome to the Shelter Bot!" + " :blush:");
        log.info("Заходил пользователь: " + name);
        sendText(chatId, answer);
    }

    /**
     * method for registration user
     *
     * @param message can't be <b>null</b>
     */
    private void registerUser(@NotNull Message message) {
        var chat = message.getChat();
        var chatId = chat.getId();
        var name = chat.getFirstName();

        if (userRepository.findById(chatId).isEmpty()) {
            var user = new User();
            user.setChatId(chatId);
            user.setName(name);
            user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));

            userRepository.save(user);
            log.info("User saved: " + user);
        }
    }

    /**
     * method receives chatId and any text
     * and executing it
     * <br>
     * use method telegram's library {@link SendMessage message = new SendMessage();}
     *
     * @param chatId can't be <b>null</b>
     */
    private void sendText(Long chatId, String text) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeSendMessage(message);
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
     * method execute message
     *
     * @param message message
     */
    private void executeSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void callVolunteer(Long chatId) {
//        var message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(TEXT_OF_VOLUNTEER);//
//        executeSendMessage(message);

        sendText(chatId, TEXT_OF_VOLUNTEER);
    }

    /**
     * main method branches of cat
     *
     * @param text   text
     * @param chatId chat id
     */
    private void mainMenuShelterForCat(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        message.enableMarkdown(true);
//
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        message.setReplyMarkup(keyboardMarkup);
//        keyboardMarkup.setSelective(true);
//        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        row.add(INFO_ABOUT_SHELTER_CAT);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(HOW_TAKE_ANIMAL_FROM_SHELTER);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(SEND_REPORT_ABOUT_PET);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(CALL_VOLUNTEER);
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//        message.setReplyMarkup(keyboardMarkup);
//
//        executeMessage(message);

        universalMenu(chatId, text,
                INFO_ABOUT_SHELTER_CAT,
                HOW_TAKE_ANIMAL_FROM_SHELTER,
                SEND_REPORT_ABOUT_PET,
                CALL_VOLUNTEER);
    }

    private void menuFindInformationAboutShelterCat(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        message.enableMarkdown(true);
//
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        message.setReplyMarkup(keyboardMarkup);
//        keyboardMarkup.setSelective(true);
//        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        row.add(TELL_ABOUT_SHELTER_CAT);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(LEAVE_YOUR_CONTACT_DETAILS);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(CALL_VOLUNTEER);
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//        message.setReplyMarkup(keyboardMarkup);
//
//        executeMessage(message);

        universalMenu(chatId, text,
                TELL_ABOUT_SHELTER_CAT,
                SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT,
                GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR,
                ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER,
                LEAVE_YOUR_CONTACT_DETAILS,
                CALL_VOLUNTEER);
    }

    private void menuHowTakeAnimal(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        message.enableMarkdown(true);
//
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        message.setReplyMarkup(keyboardMarkup);
//        keyboardMarkup.setSelective(true);
//        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        row.add(GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(GIVE_A_LIST_OF_DOCUMENTS_tO_ADOPt_AN_ANIMAL_FrOM_A_SHELTER);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(LEAVE_YOUR_CONTACT_DETAILS);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(CALL_VOLUNTEER);
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//        message.setReplyMarkup(keyboardMarkup);
//
//        executeMessage(message);

        universalMenu(chatId, text,
                GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS,
                GIVE_A_LIST_OF_DOCUMENTS_tO_ADOPt_AN_ANIMAL_FrOM_A_SHELTER,
                PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS,
                GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN,
                GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL,
                GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY,
                GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE,
                LEAVE_YOUR_CONTACT_DETAILS,
                CALL_VOLUNTEER);
    }

    private void menuSendReportAboutPet(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//        message.enableMarkdown(true);
//
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        keyboardMarkup.setSelective(true);
//        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
//        message.setReplyMarkup(keyboardMarkup);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        row.add(GET_DAILY_REPORT_FROM);
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//        row.add(CALL_VOLUNTEER);
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//        message.setReplyMarkup(keyboardMarkup);
//
//        executeMessage(message);

        universalMenu(chatId, text,
                GET_DAILY_REPORT_FROM,
                "Отправить отчёт",
                CALL_VOLUNTEER);
    }

    private void universalMenu(Long chatId, String text, @NotNull String... stringsForRows) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (var string : stringsForRows) {
            var row = new KeyboardRow();
            row.add(string);
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        executeSendMessage(message);
    }

    private boolean handleReport(Message message) {
        var chatId = message.getChat().getId();

        var user = userRepository.findByChatId(chatId).orElseThrow();
        var lastCommand = user.getLastCommand();

        if (!lastCommand.equals("Отправить отчёт")) {
            log.error("Неверный запуск обработчика!");
            return false;
        }

        if (!message.hasPhoto()) {
            sendText(chatId, "Вы не добавили фото к отчёту!");
            return false;
        }

        if (!message.hasText()) {
            sendText(chatId, "Вы не добавили текст к отчёту!");
            return false;
        }
        var text = message.getText();
        var photoId = message.getPhoto().get(0).getFileUniqueId();

        var report = new Report();
        report.setUser(user);
        report.setPhotoId(photoId);
        report.setText(text);
        report.setDateTime(LocalDateTime.now());
        reportService.add(report);
        return true;
    }
}
