package pro.sky.telegrambot.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.Notification;
import pro.sky.telegrambot.service.NotificationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTimer {
    private final NotificationService notificationService;
    private final TelegramBot telegramBot;

    public NotificationTimer(NotificationService notificationService, TelegramBot telegramBot) {
        this.notificationService = notificationService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void sendNotification() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        notificationService.getAllNotificationsForDate(now).forEach(
                n -> telegramBot.execute(new SendMessage(n.getChatId(), String.format("вы просили напомнить о %s", n.getMessage()))));
    }
@Scheduled
    public void deleteNotification(Notification notification){
       notificationService.deleteNotification(notification);
}


}



