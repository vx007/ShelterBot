package com.skypro.shelterbot.service;

import com.skypro.shelterbot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;
    static final String HELP_TEXT = "Этот бот создан для реализации возможностей.\n\n" +
            "Можно реализовать команды из основного меню или типы команд.\n\n" +
            "Type /start чтобы увидеть приветсятвеное сообщение.\n\n" +
            "Type /mydata чтобы увидеть инфо о себе.\n\n" +
            "Type /help увидеть это сообщение снова.";

    public TelegramBot(BotConfig config) {
        this.botConfig = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "получаем приветственое сообщение"));
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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                default:
                    sendMessage(chatId, "Sorry, command was't recognized  ");
            }
        }
    }

    /**
     * <u>текст ниже написан для примера некоторых возможностей</u>
     * <b>получаем</b> <i>приветственое</i> <u>сообщение</u>  <b>жирный</b> <i>курсив</i> <u>подчеркнутный</u>
     * <br>
     * Используется метод {@link SendMessage#setChatId(Long)}
     * botConfig.getBotName(); <br>
     * {@code botConfig.getBotName();}
     * {@link #HELP_TEXT}
     * {@value  #HELP_TEXT}
     *
     * @param chatId идентификатор приветственого сообщения, can't be <u>{@code null}<u/>
     * @throws TelegramApiException <u>example</u> there is no it exception
     * @return <u>example</u> этот метод не возвращает
     *
     * @see <u>example</u> SendMessage#setChatId(Long)
     */
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
