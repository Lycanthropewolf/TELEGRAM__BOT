package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificationService;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {
    private static final String HELP_TEXT = "Привет, я могу напомнить тебе о дз, для этого отправь сообщение в формате'сделать домашнюю работу'";
    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern NOTIFICATION_PATTERN = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
    private final TelegramBot telegramBot;
    private static final DateTimeFormatter NOTIFICATION_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.mm.yyyy HH:mm");

    private final NotificationService notificationService;
    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationService notificationService) {
        this.telegramBot = telegramBot;
        this.notificationService = notificationService;
    }


    @Override
    public int process(List<Update> updates) {
        updates.stream().filter(update -> update.message() != null).forEach(this::handleUpdate);
        return CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(Update update) {
        if (update.message().text() != null) {
            processText(update);
        } else {
            this.defaultMessage(update.message().chat().id());
        }
    }

    private void processText(Update update) {
        String text = update.message().text();
        Long chatId = update.message().chat().id();
        switch (text) {
            case "/start":
                sendMessage(chatId, String.format("Привет, %s, %s", update.message().from().firstName(), HELP_TEXT));

            case "/help":
                sendMessage(chatId, HELP_TEXT);

            default:
                Matcher matcher = NOTIFICATION_PATTERN.matcher(text);
                if (matcher.matches()) {
                    this.handleNotification(chatId, matcher.group(1), matcher.group(3));
                } else {
                    this.defaultMessage(chatId);
                }
        }
    }

    private void handleNotification(Long chatId, String dateString, String notificationMessage) {
        LOG.info("Got notification message, chat_id={},date={},notification={}", chatId, dateString, notificationMessage);
        try {
            LocalDateTime notificationDate = LocalDateTime.parse(dateString, NOTIFICATION_DATE_FORMATTER);
            notificationService.createNotification(chatId,notificationDate,notificationMessage);
            this.sendMessage(chatId,"Запись создана");
        }catch (DateTimeException e){
            LOG.error("Введите правильно время и дату, chat_id={}, date={}",chatId,dateString,e);
            this.sendMessage(chatId,"неверная дата, попробуйте /help");
        }

    }

    private void defaultMessage(Long chatId) {
        this.sendMessage(chatId, "Неизвестный формат сообщения - попробуйте /help");
    }

    private void sendMessage(Long chatId, String text) {
        this.telegramBot.execute(new SendMessage(chatId, text));
    }


}



