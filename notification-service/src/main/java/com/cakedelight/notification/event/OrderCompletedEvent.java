package com.cakedelight.notification.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCompletedEvent(Long orderId, BigDecimal totalAmount, LocalDateTime createdAt) {
}
