package pro.sky.telegrambot.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.Notification;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void createNotification(Long chatId, LocalDateTime notificationDate, String notificationMessage) {
        Notification notification = new Notification();
        notification.setChatId(chatId);
        notification.setDate(notificationDate.truncatedTo(ChronoUnit.MINUTES));
        notification.setMessage(notificationMessage);
        repository.save(notification);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Notification repository content:");
            repository.findAll().stream().forEach(not -> {
                LOG.trace("Notification={}", not);
            });
        }
    }

    public List<Notification> getAllNotificationsForDate(LocalDateTime date) {
        return repository.findAllByDateEquals(date.truncatedTo(ChronoUnit.MINUTES));
    }
}
