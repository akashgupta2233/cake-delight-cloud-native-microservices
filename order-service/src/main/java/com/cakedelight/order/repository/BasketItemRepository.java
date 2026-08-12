package com.cakedelight.order.repository;

import com.cakedelight.order.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for basket item data access.
 * Provides methods for managing shopping basket items.
 */
@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    Optional<BasketItem> findByCakeId(Long cakeId);

    List<BasketItem> findAllByOrderByIdAsc();
}
