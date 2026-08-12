package com.cakedelight.order.repository;

import com.cakedelight.order.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    Optional<BasketItem> findByCakeId(Long cakeId);

    List<BasketItem> findAllByOrderByIdAsc();
}
