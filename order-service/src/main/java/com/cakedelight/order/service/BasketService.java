package com.cakedelight.order.service;

import com.cakedelight.order.entity.BasketItem;
import com.cakedelight.order.exception.BasketItemNotFoundException;
import com.cakedelight.order.repository.BasketItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasketService {

    private final BasketItemRepository basketItemRepository;

    public BasketService(BasketItemRepository basketItemRepository) {
        this.basketItemRepository = basketItemRepository;
    }

    public BasketItem create(BasketItem item) {
        item.setId(null);
        return basketItemRepository.save(item);
    }

    public List<BasketItem> findAll() {
        return basketItemRepository.findAll();
    }

    public BasketItem findById(Long id) {
        return basketItemRepository.findById(id).orElseThrow(() -> new BasketItemNotFoundException(id));
    }

    public BasketItem update(Long id, BasketItem payload) {
        BasketItem existing = findById(id);
        existing.setCakeId(payload.getCakeId());
        existing.setCakeName(payload.getCakeName());
        existing.setPrice(payload.getPrice());
        existing.setQuantity(payload.getQuantity());
        return basketItemRepository.save(existing);
    }

    public void delete(Long id) {
        BasketItem existing = findById(id);
        basketItemRepository.delete(existing);
    }

    public void clearAll() {
        basketItemRepository.deleteAll();
    }
}