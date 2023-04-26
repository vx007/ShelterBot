package com.skypro.shelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.shelterbot.exeption.ShelterBotSendTextException;
import com.skypro.shelterbot.resource.ShelterBotStrings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ShelterBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ShelterBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(@NotNull List<Update> updates) {
        int processedUpdates = 0;
        try {
            for (Update update : updates) {
                handle(update);
                processedUpdates++;
            }
        } catch (ShelterBotSendTextException e) {
            logger.error("{}, КОД: {}", e.getMessage(), e.getErrorCode());
            if (processedUpdates == 0) {
                return UpdatesListener.CONFIRMED_UPDATES_NONE;
            } else {
                return processedUpdates;
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendText(Long chatId, String text) throws ShelterBotSendTextException {
        logger.info("Отправка текстового сообщения, ЧАТИД: {}, ТЕКСТ: {}", chatId, text);
        SendMessage message = new SendMessage(chatId, text);
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            throw new ShelterBotSendTextException("Ошибка передачи текстового сообщения", response.errorCode());
        }
    }

    private void handle(Update update) throws ShelterBotSendTextException {
        logger.info("Обработка обновления, ИД: {}", update.updateId());
        Message message = update.message();
        Long chatId = message.chat().id();
        String text = message.text();

        if ("/start".equals(text)) {
            sendText(chatId, ShelterBotStrings.HELLO);
        }

        if ("/help".equals(text)) {
            sendText(chatId, ShelterBotStrings.HELP);
        }
    }
}
