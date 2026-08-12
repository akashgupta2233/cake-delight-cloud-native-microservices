package com.cakedelight.order.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event record representing a completed order.
 * Published to RabbitMQ to notify downstream services.
 */
public record OrderCompletedEvent(Long orderId, BigDecimal totalAmount, LocalDateTime createdAt) {
}
