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
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    final BotConfig config;
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String CAT_BUTTON = "CAT_BUTTON";
    static final String DOG_BUTTON = "DOG_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    static final String HELP_TEXT = "Этот бот создан для реализации возможностей.\n\n" +
            "Можно реализовать команды из основного меню или типы команд.\n\n" +
            "Type /start чтобы увидеть приветсятвеное сообщение.\n\n" +
            "Type /mydata чтобы увидеть инфо о себе.\n\n" +
            "Type /help увидеть это сообщение снова.";

    static final String INFO_ABOUT_SHELTER_CAT = "информация о приюте кошек";
    static final String HOW_TO_ADOPT_AN_ANIMAL = "информация how to adopt an animal from a shelter";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "получаем приветственое сообщение"));
        listOfCommands.add(new BotCommand("/cat", "cat"));
        listOfCommands.add(new BotCommand("/dog", "dog"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
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
                        offerChoice(chatId);
                        break;

                    case "/cat":
                        sendMessage(chatId,INFO_ABOUT_SHELTER_CAT);

                        break;

                    case "/dog":

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
                String text = "Yue pressed CAT button";
                executeEditMessageText(text, chatId, messageId);
                sendMessage(chatId,INFO_ABOUT_SHELTER_CAT);
                getInlineKeyboardMarkup();

            } else if (callBackData.equals(DOG_BUTTON)) {
                String text = "Yue pressed DOG button";
                sendMessage(chatId,text);
            }

        }
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

    private  InlineKeyboardMarkup getInlineKeyboardMarkup() {

        InlineKeyboardMarkup markupButtons = new InlineKeyboardMarkup();

        InlineKeyboardButton infoAboutShelterCat = new InlineKeyboardButton(INFO_ABOUT_SHELTER_CAT);
        InlineKeyboardButton howToAdoptAnAnimal = new InlineKeyboardButton(HOW_TO_ADOPT_AN_ANIMAL);

        infoAboutShelterCat.setCallbackData(INFO_ABOUT_SHELTER_CAT);
        howToAdoptAnAnimal.setCallbackData(HOW_TO_ADOPT_AN_ANIMAL);

        List<InlineKeyboardButton> listButtonRow1 = new ArrayList<>();
        listButtonRow1.add(infoAboutShelterCat);

        List<InlineKeyboardButton> listButtonRow2 = new ArrayList<>();
        listButtonRow2.add(howToAdoptAnAnimal);

        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        listOfButtons.add(listButtonRow1);
        listOfButtons.add(listButtonRow2);

        markupButtons.setKeyboard(listOfButtons);
        return markupButtons;
    }

    private void offerChoice(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("выберите приют");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> listButton = new ArrayList<>();

        var catButton = new InlineKeyboardButton();
        catButton.setText("Cat");
        catButton.setCallbackData(CAT_BUTTON);

        var dogButton = new InlineKeyboardButton();
        dogButton.setText("Dog");
        dogButton.setCallbackData(DOG_BUTTON);

        listButton.add(catButton);
        listButton.add(dogButton);


        listOfButtons.add(listButton);
        inlineKeyboardMarkup.setKeyboard(listOfButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);


        executeMessage(message);
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
            //user.setRegisteredAt(new TimeStamp(currentTimeMillis()));
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

    // метод для вызова доп клавиатуры с кнопками
    private void methodCallButtons(SendMessage message) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("информация о приложении");
        row.add("регистрация");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("проверить мои данные");
        row.add("удалить мои данные");
        row.add("Как взять животное из приюта");
        row.add("Прислать отчёт о питомце");
        row.add("Позвать волонтёра");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
    }
}
