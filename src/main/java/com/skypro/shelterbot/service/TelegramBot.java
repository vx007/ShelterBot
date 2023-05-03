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

@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    final BotConfig config;
    static final String CAT_BUTTON = "CAT_BUTTON";
    static final String DOG_BUTTON = "DOG_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    static final String HELP_TEXT = "Этот бот создан для реализации возможностей.\n\n" +
            "Можно реализовать команды из основного меню или типы команд.\n\n" +
            "Type /start чтобы увидеть приветсятвеное сообщение.\n\n" +
            "Type /mydata чтобы увидеть инфо о себе.\n\n" +
            "Type /help увидеть это сообщение снова. И т.д.";
    static final String INFO_ABOUT_SHELTER_CAT = "тут должна быть инфа о приюте кошек";
    static final String INFO_HOW_TAKE_ANIMAL = "тут должна быть инфа Как взять животное";

    static final String ABOUT_SHELTER_CAT = "О приюте кошек";
    static final String HOW_TAKE_ANIMAL = "Как взять животное";
    static final String SEND_INFO_ABOUT_PET = "Прислать отчет о питомце";
    static final String CALL_VOLUNTEER = "Позвать волонтера";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Welcome to the Shelter Bot!"));
        listOfCommands.add(new BotCommand("/cat", "О приюте кошек"));
        listOfCommands.add(new BotCommand("/volunteer", "волонтер"));
        listOfCommands.add(new BotCommand("/help", "how to use this bot"));
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
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
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

                    case "/cat":
                        offerChoiceMenuAboutShelterCat(chatId);
                        break;

                    case "/volunteer":
                        callVolunteer(chatId);
                        break;
                    case "Рассказать о приюте":

                        break;
                    case "Выдать расписание работы приюта и адрес, схему проезда":

                        break;
                    case "Выдать контактные данные охраны для оформления пропуска на машину":

                        break;
                    case "Выдать общие рекомендации о технике безопасности на территории приюта":

                        break;
                    case "Принять и записать контактные данные для связи":

                        break;

                    case "/help":
                        sendMessage(chatId, HELP_TEXT);
                        break;

                    default:
                       sendMessage(chatId, "Sorry, command was't recognized  ");
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();

            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(CAT_BUTTON)) {
                String text = EmojiParser.parseToUnicode("Вы выбрали приют кошек! " + " :cat:");
                executeEditMessageText(text, chatId, messageId);
                offerChoiceMenuAboutShelterCat(chatId);

            } else if (callBackData.equals(DOG_BUTTON)) {
                String text = EmojiParser.parseToUnicode("Вы выбрали приют собак! " + ":dog:");
                executeEditMessageText(text, chatId, messageId);

            } else if (callBackData.equals(ABOUT_SHELTER_CAT)) {
                String text = EmojiParser.parseToUnicode("Информация о приюте кошек: :cat: ");
//                String text = EmojiParser.parseToUnicode("Информация о приюте кошек: :cat: \n\n" + INFO_ABOUT_SHELTER_CAT);
                executeEditMessageText(text, chatId, messageId);
                sendMessage(chatId,"Привет, "+ update.getCallbackQuery().getMessage().getChat().getFirstName() + " выбери из меню");
                botWelcomesUser(text,chatId,messageId);


            } else if (callBackData.equals(HOW_TAKE_ANIMAL)) {
                String text = "Вы выбрали, как взять животное";
                executeEditMessageText(text,chatId,messageId);
                sendMessage(chatId,"Привет, "+ update.getCallbackQuery().getMessage().getChat().getFirstName() + " выбери из меню");


            } else if (callBackData.equals(SEND_INFO_ABOUT_PET)) {
                String text = "Вы выбрали, отправить информацию о петомце";
                executeEditMessageText(text,chatId,messageId);
                sendMessage(chatId,"Привет, "+ update.getCallbackQuery().getMessage().getChat().getFirstName() + " выбери из меню");


            } else if (callBackData.equals(CALL_VOLUNTEER)) {
                String text = "Вы выбрали, позвать волонтера";
                executeEditMessageText(text,chatId,messageId);
                callVolunteer(chatId);

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
        List<InlineKeyboardButton> listButtonSecond = new ArrayList<>();

        var catButton = new InlineKeyboardButton();
        catButton.setText("приют кошек");
        catButton.setCallbackData(CAT_BUTTON);

        var dogButton = new InlineKeyboardButton();
        dogButton.setText("приют собак");
        dogButton.setCallbackData(DOG_BUTTON);

        listButtonFirst.add(catButton);
        listButtonSecond.add(dogButton);

        listOfButtons.add(listButtonFirst);
        listOfButtons.add(listButtonSecond);
        inlineKeyboardMarkup.setKeyboard(listOfButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void offerChoiceMenuAboutShelterCat(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("выберите из меню");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> listButtonFirst = new ArrayList<>();
        List<InlineKeyboardButton> listButtonSecond = new ArrayList<>();
        List<InlineKeyboardButton> listButtonThird = new ArrayList<>();
        List<InlineKeyboardButton> listButtonFourth = new ArrayList<>();

        var aboutShelterCat = new InlineKeyboardButton();
        aboutShelterCat.setText("О приюте кошек");
        aboutShelterCat.setCallbackData(ABOUT_SHELTER_CAT);

        var howTakeAnimal = new InlineKeyboardButton();
        howTakeAnimal.setText("Как взять животное");
        howTakeAnimal.setCallbackData(HOW_TAKE_ANIMAL);

        var sendInfoAboutAnimal = new InlineKeyboardButton();
        sendInfoAboutAnimal.setText("Прислать отчет о питомце");
        sendInfoAboutAnimal.setCallbackData(SEND_INFO_ABOUT_PET);

        var callVolunteer = new InlineKeyboardButton();
        callVolunteer.setText("Позвать волонтера");
        callVolunteer.setCallbackData(CALL_VOLUNTEER);

        listButtonFirst.add(aboutShelterCat);
        listButtonSecond.add(howTakeAnimal);
        listButtonThird.add(sendInfoAboutAnimal);
        listButtonFourth.add(callVolunteer);

        listOfButtons.add(listButtonFirst);
        listOfButtons.add(listButtonSecond);
        listOfButtons.add(listButtonThird);
        listOfButtons.add(listButtonFourth);
        inlineKeyboardMarkup.setKeyboard(listOfButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId,  long messageId) { // меотод показывает что нажал
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




    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, welcome to the Shelter Bot!" + " :blush:");
        log.info("Заходил пользователь: " + name);
        sendMessage(chatId, answer);
    }

    private void registeredUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setName(user.getName());
            user.setRegisteredAt(new Timestamp(currentTimeMillis()));
            user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }


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
        message.setText("Здравствуйте я волонтер, чё те надо?");
        executeMessage(message);
    }

    // метод для вызова доп клавиатуры с кнопками
    private void botWelcomesUser(String text, long chatId,  long messageId) {
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
        row.add("Рассказать о приюте");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Выдать контактные данные охраны для оформления пропуска на машину");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Выдать общие рекомендации о технике безопасности на территории приюта");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Принять и записать контактные данные для связи");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Выдать расписание работы приюта и адрес, схему проезда");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }
}
