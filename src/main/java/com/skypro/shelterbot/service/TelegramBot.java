package com.skypro.shelterbot.service;

import com.skypro.shelterbot.config.BotConfig;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repositories.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * @extends {@link TelegramLongPollingBot} идет проверка не написали ли что то
 */
@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {


    @Autowired
    private UserRepository userRepository;

    final BotConfig config;

    static final String ERROR_TEXT = "Error occurred: ";
    static final String HELP_TEXT = "Этот бот создан, что бы вы смогли найти себе подходящего петомца \n\n" +
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


    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы с Shelter Bot!"));
        listOfCommands.add(new BotCommand("/help", "как использовать бота"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    /**
     * Update класс получает сообщение от пользователя боту,
     * также хвранит всю всю инфо о нем
     *
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId) { //отправка всем пользователям сообщения от проверенного пользователя
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));// метод substring позволяет получить текст после /send
                var users = userRepository.findAll();
                for (User user : users) {
                    sendMessage(user.getChatId(), textToSend);
                }
            } else {

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        registeredUser(update.getMessage());
                        offerCatOrDog(chatId);
                        break;

                    case "/volunteer":
                        callVolunteer(chatId);
                        break;
//                       1.

                    case INFO_ABOUT_SHELTER_CAT:
                        menuFindInformationAboutShelterCat("Привет!" + update.getMessage().getChat().getFirstName(), chatId);
                        break;

                    case HOW_TAKE_ANIMAL_FROM_SHELTER:
                        menuHowTakeAnimal("Привет!" + update.getMessage().getChat().getFirstName(), chatId);
                        break;

                    case SEND_REPORT_ABOUT_PET:
                        menuSendReportAboutPet("Привет!" + update.getMessage().getChat().getFirstName(), chatId);
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
                        sendMessage(chatId,update.getMessage().getChat().getFirstName() + ", оставьте свои данные!");
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

                    case "/help":
                        sendMessage(chatId, HELP_TEXT);
                        break;

                    default:
                        sendMessage(chatId, "Sorry, command was't recognized \n\n" + HELP_TEXT);
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();

            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(CAT_BUTTON)) {
                String text = EmojiParser.parseToUnicode("Вы выбрали приют кошек! " + " :cat:");
                mainMenuShelterForCat(text, chatId, messageId);

            } else if (callBackData.equals(DOG_BUTTON)) {
                String text = EmojiParser.parseToUnicode("Вы выбрали приют собак! " + " :dog:" + "\n\nУпс эта функция в разработке!  ");
                executeEditMessageText(text, chatId, messageId);

            }
        }
    }

    private void offerCatOrDog(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("выберите из меню");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> listButtonFirst = new ArrayList<>();

        var catButton = new InlineKeyboardButton();
        catButton.setText(EmojiParser.parseToUnicode("приют кошек " + " :cat:"));
        catButton.setCallbackData(CAT_BUTTON);

        var dogButton = new InlineKeyboardButton();
        dogButton.setText(EmojiParser.parseToUnicode("приют собак " + " :dog:"));
        dogButton.setCallbackData(DOG_BUTTON);

        listButtonFirst.add(catButton);
        listButtonFirst.add(dogButton);

        listOfButtons.add(listButtonFirst);
        inlineKeyboardMarkup.setKeyboard(listOfButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) { // меотод показывает что нажал
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    /**
     * welcome message to new user
     * @param chatId can't be <b>null</b>
     * @param name can't be <b>null</b>
     */
    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, welcome to the Shelter Bot!" + " :blush:");
        log.info("Заходил пользователь: " + name);
        sendMessage(chatId, answer);
    }

    /**
     * method for registration user
     * @param msg can't be <b>null</b>
     */
    private void registeredUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setName(chat.getFirstName()); //так мы сразу получаем имя
//            user.setName(user.getName());
            user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    /**
     * method receives chatId and any text
     * and executing it
     * <br>
     * use method telegram's library {@link SendMessage message = new SendMessage();}
     * @param chatId can't be <b>null</b>
     *  @throws TelegramApiException can throw
     */
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

    /**
     * method execute message
     * @param message
     */
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void callVolunteer(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(TEXT_OF_VOLUNTEER);

        executeMessage(message);
    }

    /**
     * main method branches of cat
     * @param text
     * @param chatId
     * @param messageId
     */
    private void mainMenuShelterForCat(String text, long chatId, long messageId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(INFO_ABOUT_SHELTER_CAT);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(HOW_TAKE_ANIMAL_FROM_SHELTER);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(SEND_REPORT_ABOUT_PET);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(CALL_VOLUNTEER);
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void menuFindInformationAboutShelterCat(String text, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(TELL_ABOUT_SHELTER_CAT);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(SCHEDULE_ADDRESS_DIRECTION_ABOUT_SHELTER_CAT);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(GIVE_CONTACT_DETAILS_OF_THE_GUARDS_FOR_ISSUING_A_PASS_FOR_THE_CAR);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(ISSUE_GENERAL_SAFETY_ADVICE_AT_THE_SHELTER);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(LEAVE_YOUR_CONTACT_DETAILS);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(CALL_VOLUNTEER);
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void menuHowTakeAnimal(String text, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(GIVE_THE_RULES_OF_ACQUAINTANCE_WITH_ANIMALS);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(GIVE_A_LIST_OF_DOCUMENTS_tO_ADOPt_AN_ANIMAL_FrOM_A_SHELTER);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(PRODUCE_A_LIST_OF_TRANSPORTATION_RECOMMENDATIONS);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_A_KITTEN);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(GIVE_A_LIST_OF_RECOMMENDATIONS_FOR_HOME_IMPROVEMENT_FOR_AN_ADULT_ANIMAL);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(GIVE_A_LIST_OF_HOME_IMPROVEMENT_RECOMMENDATIONS_FOR_AN_ANIMAL_WITH_A_DISABILITY);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(GIVE_A_LIST_OF_REASONS_WHY_THEY_CAN_REFUSE);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(LEAVE_YOUR_CONTACT_DETAILS);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(CALL_VOLUNTEER);
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void menuSendReportAboutPet(String text, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(GET_DAILY_REPORT_FROM);
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add(CALL_VOLUNTEER);
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

}
