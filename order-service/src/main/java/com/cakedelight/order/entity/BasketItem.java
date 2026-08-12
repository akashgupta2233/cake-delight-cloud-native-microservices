package com.cakedelight.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing an item in the shopping basket.
 * Stores cake details and quantity before checkout.
 */
@Entity
@Table(name = "basket_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long cakeId;

    @NotBlank
    private String cakeName;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private Integer quantity;
}
