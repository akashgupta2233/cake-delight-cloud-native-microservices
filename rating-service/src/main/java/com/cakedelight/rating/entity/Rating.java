package com.cakedelight.rating.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "cakeId is required")
    private Long cakeId;

    @NotNull(message = "ratingValue is required")
    @Min(value = 1, message = "ratingValue must be at least 1")
    @Max(value = 5, message = "ratingValue must be at most 5")
    private Integer ratingValue;

    @Column(length = 1000)
    private String review;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
