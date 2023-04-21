package com.skypro.shelterbot.service;

import com.skypro.shelterbot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @extends {@link TelegramLongPollingBot} идет проверка не написали ли что то
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;

    public TelegramBot(BotConfig config) {
        this.botConfig = config;
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

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    sendMessage(chatId, "Sorry, command was't recognized  ");
            }
        }
    }

    /**
     * welcome message to new user
     * @param chatId can't be <b>null</b>
     * @param name can't be <b>null</b>
     */
    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", nice to meet you!";

        //TODO add check if user exists
        sendMessage(chatId, answer);
    }

    /**
     * send id users and text for him.
     * <br>
     * use method telegram's library {@link SendMessage message = new SendMessage();}
     * @param chatId can't be <b>null</b>
     *  @throws TelegramApiException can throw
     */
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {

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
