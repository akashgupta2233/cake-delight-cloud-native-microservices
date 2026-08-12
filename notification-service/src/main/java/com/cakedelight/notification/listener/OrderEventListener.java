package com.cakedelight.notification.listener;

import com.cakedelight.notification.event.OrderCompletedEvent;
import com.cakedelight.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ listener for order-related events.
 * Consumes order completed events and triggers notification creation.
 */
@Component
public class OrderEventListener {

    private final NotificationService notificationService;
    private final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    public OrderEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Handles order completed events from RabbitMQ queue.
     * Creates and persists notification record.
     *
     * @param event the order completed event
     */
    @RabbitListener(queues = "notification.queue")
    public void handleOrderCompleted(OrderCompletedEvent event) {
        notificationService.createNotificationFromEvent(event);
        log.info("Notification sent for order {}", event.orderId());
    }
}
