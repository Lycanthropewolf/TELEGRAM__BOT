package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.Service.UserService;
import pro.sky.telegrambot.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class  TelegramBotUpdatesListener implements UpdatesListener {

    private static final  Logger LOG = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final  TelegramBot telegramBot;
    private final  UserService userService;

    private final  Map<Long, Status> usersStatus = new HashMap<>();

    public  TelegramBotUpdatesListener(TelegramBot telegramBot, UserService userService) {
        this.telegramBot = telegramBot;
        this.userService = userService;
    }


    @Override
    public int  process(List<Update> updates) {
        updates.stream().filter(update -> update.message() != null).forEach(this::handleUpdate);
        return CONFIRMED_UPDATES_ALL;
    }

    private void  handleUpdate(Update update) {
        Long chatId = update.message().chat().id();
        String text = update.message().text();
        Status status = usersStatus.get(update.message().chat().id());
        if (status != null) {
            switch (status) {
                case LOGIN: {
                    userService.addLogin(chatId, text);
                    usersStatus.put(chatId, Status.LOGIN);
                    telegramBot.execute(new SendMessage(chatId, "введите пароль"));
                }
                case PASSWORD: {
                    userService.addPassword(chatId, text);
                    usersStatus.put(chatId, Status.PASSWORD);
                    telegramBot.execute(new SendMessage(chatId, "Ввод успешен"));

                }
                case DATA_ENTERED: {
                    telegramBot.execute(new SendMessage(chatId, userService.getUser(chatId).toString()));
                }
            }
        } else  if (text.equals("/start")) {
            usersStatus.put(chatId, Status.LOGIN);
            telegramBot.execute(new SendMessage(chatId, "Почему я ничего не понимаю ?"));
        }
    }

}



