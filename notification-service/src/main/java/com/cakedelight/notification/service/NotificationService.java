package com.cakedelight.notification.service;

import com.cakedelight.notification.entity.Notification;
import com.cakedelight.notification.event.OrderCompletedEvent;

import java.util.List;

public interface NotificationService {
    Notification createNotificationFromEvent(OrderCompletedEvent event);

    List<Notification> getAllNotifications();
}
