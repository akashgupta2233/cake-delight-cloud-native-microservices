package com.cakedelight.notification.service;

import com.cakedelight.notification.entity.Notification;
import com.cakedelight.notification.event.OrderCompletedEvent;
import com.cakedelight.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for notification management.
 * Handles creation and retrieval of notification records.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates a notification record from an order completed event.
     *
     * @param event the order completed event
     * @return the persisted notification
     */
    @Override
    public Notification createNotificationFromEvent(OrderCompletedEvent event) {
        String msg = String.format("Order %d completed with amount %s", event.orderId(), event.totalAmount());
        Notification n = Notification.builder()
                .orderId(event.orderId())
                .message(msg)
                .status("SENT")
                .build();
        return notificationRepository.save(n);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
