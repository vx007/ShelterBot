package com.skypro.shelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.shelterbot.entity.Chat;
import com.skypro.shelterbot.exeption.SendTextException;
import com.skypro.shelterbot.resource.TextStrings;
import com.skypro.shelterbot.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class BotListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final ChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BotListener(TelegramBot telegramBot, ChatService chatService) {
        this.telegramBot = telegramBot;
        this.chatService = chatService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        int processedUpdates = 0;
        try {
            for (Update update : updates) {
                handle(update);
                processedUpdates++;
            }
        } catch (SendTextException e) {
            logger.error("{}, КОД: {}", e.getMessage(), e.getErrorCode());
            if (processedUpdates == 0) {
                return UpdatesListener.CONFIRMED_UPDATES_NONE;
            } else {
                return processedUpdates;
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendText(Long chatId, String text) throws SendTextException {
        logger.info("Отправка текстового сообщения, ЧАТ: {}, ТЕКСТ: {}", chatId, text);
        SendMessage message = new SendMessage(chatId, text);
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            throw new SendTextException("Ошибка передачи текстового сообщения", response.errorCode());
        }
    }

    private void handle(Update update) throws SendTextException {
        logger.info("Обработка обновления, ИД: {}", update.updateId());
        Message message = update.message();
        Long chatId = message.chat().id();
        String text = message.text();

        if ("/start".equals(text)) {
            if (chatService.find(chatId).isEmpty()){
                Chat chat = new Chat(chatId, "user", "/start");
                chatService.add(chat);
                sendText(chatId, TextStrings.HELLO);
            } else {
                sendText(chatId, TextStrings.NO_HELLO);
            }
        }

        if ("/help".equals(text)) {
            if (chatService.find(chatId).isPresent()){
                Chat chat = chatService.find(chatId).get();
                chat.setLastCommand("/help");
                chatService.add(chat);
            }

            sendText(chatId, TextStrings.HELP);
        }

        if ("/status".equals(text)){
            if (chatService.find(chatId).isPresent()){
                Chat chat = chatService.find(chatId).get();
                chat.setLastCommand("/status");
                sendText(chatId, TextStrings.STATUS + chat.getStatus());
            } else {
                sendText(chatId, TextStrings.NO_STATUS);
            }
        }
    }
}
