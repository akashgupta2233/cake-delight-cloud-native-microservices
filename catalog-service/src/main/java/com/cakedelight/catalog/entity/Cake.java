package com.cakedelight.catalog.entity;

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

@Entity
@Table(name = "cakes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    private String category;

    @NotNull
    @Positive
    private BigDecimal price;

    private Boolean availability = Boolean.TRUE;

    @Column(length = 1000)
    private String imageUrl;
}