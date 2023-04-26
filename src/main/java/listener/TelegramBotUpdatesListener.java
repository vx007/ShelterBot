package listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
//        updates.stream()
//                .filter(update -> update.message() != null || update.callbackQuery() != null)
//                .forEach(this::handleUpdate);
//
//        return CONFIRMED_UPDATES_ALL;
//    }
//
//    private void handleUpdate(Update update){
//        if (update.message().text().startsWith("/start")){
//            this.telegramBot.execute(
//                    new SendMessage(update.message().chat().id(), "Привет!")
//                            .entities(new MessageEntity(MessageEntity.Type.code, 0, 3)));
//            return;
//        }
//        if (update.message().text().startsWith("/Клавиатура")){
//            InlineKeyboardButton ok = new InlineKeyboardButton("OK").callbackData("/ok");
//            InlineKeyboardButton cancel = new InlineKeyboardButton("Отменить").callbackData("/Отменить");
//            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(ok, cancel);
//            this.telegramBot.execute(new SendMessage(update.message().chat().id(), "Привет")
//                    .replyMarkup(keyboard));
//        }
//        if (update.callbackQuery().message().text().startsWith("/ok")){
//            this.telegramBot.execute(new SendMessage(update.message().chat().id(), "Нажата кнопка OK"));
//        }
//
//    }


        try {
            updates.forEach(update -> {
                logger.info("Обрабатывает обновление: {}", update);
                Message message = update.message();
                Long chatId = message.chat().id();
                String text = message.text();

                if ("/start".equals(text)) {
                    SendMessage sendMessage = new SendMessage(chatId, "Привет!");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                    if (sendResponse.isOk()) {
                        logger.error("Ошибка при отправке сообщения: {}", sendResponse.description());
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}


