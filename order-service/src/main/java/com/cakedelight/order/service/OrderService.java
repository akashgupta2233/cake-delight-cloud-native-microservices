package com.cakedelight.order.service;

import com.cakedelight.order.entity.BasketItem;
import com.cakedelight.order.entity.CustomerOrder;
import com.cakedelight.order.entity.OrderItem;
import com.cakedelight.order.event.OrderCompletedEvent;
import com.cakedelight.order.event.OrderEventPublisher;
import com.cakedelight.order.repository.BasketItemRepository;
import com.cakedelight.order.repository.CustomerOrderRepository;
import com.cakedelight.order.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final BasketItemRepository basketItemRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventPublisher orderEventPublisher;

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(BasketItemRepository basketItemRepository,
                        CustomerOrderRepository customerOrderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderEventPublisher orderEventPublisher) {
        this.basketItemRepository = basketItemRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public CustomerOrder checkout() {
        List<BasketItem> items = basketItemRepository.findAll();
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Basket is empty");
        }
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomerOrder order = CustomerOrder.builder()
                .totalAmount(total)
                .build();

        CustomerOrder savedOrder = customerOrderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (BasketItem bi : items) {
            OrderItem oi = OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .cakeId(bi.getCakeId())
                    .cakeName(bi.getCakeName())
                    .price(bi.getPrice())
                    .quantity(bi.getQuantity())
                    .build();
            orderItems.add(oi);
        }

        orderItemRepository.saveAll(orderItems);

        // publish event (do not let failures break checkout)
        try {
            OrderCompletedEvent event = new OrderCompletedEvent(savedOrder.getId(), savedOrder.getTotalAmount(), LocalDateTime.now());
            orderEventPublisher.publishOrderCompleted(event);
        } catch (Exception e) {
            log.warn("Error publishing OrderCompletedEvent for orderId={}", savedOrder.getId());
        }

        // clear basket
        basketItemRepository.deleteAll();

        return savedOrder;
    }
}