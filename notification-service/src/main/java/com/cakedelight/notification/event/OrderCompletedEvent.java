package com.cakedelight.notification.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event record representing a completed order.
 * Transmitted via RabbitMQ to trigger downstream notifications.
 */
public record OrderCompletedEvent(Long orderId, BigDecimal totalAmount, LocalDateTime createdAt) {
}
