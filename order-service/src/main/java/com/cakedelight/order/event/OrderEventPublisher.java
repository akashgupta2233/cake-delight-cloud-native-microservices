package com.cakedelight.order.event;

import com.cakedelight.order.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCompleted(OrderCompletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_COMPLETED_ROUTING_KEY,
                    event);
            log.info("Published OrderCompletedEvent for orderId={}", event.orderId());
        } catch (Exception e) {
            // swallow to preserve existing checkout behavior
            log.warn("Failed to publish OrderCompletedEvent for orderId={}: {}", event.orderId(), e.getMessage());
        }
    }
}
