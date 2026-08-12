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
        return basketItemRepository.findByCakeId(item.getCakeId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + 1);
                    return basketItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    item.setId(null);
                    return basketItemRepository.save(item);
                });
    }

    public List<BasketItem> findAll() {
        return basketItemRepository.findAllByOrderByIdAsc();
    }

    public BasketItem findById(Long id) {
        return basketItemRepository.findById(id).orElseThrow(() -> new BasketItemNotFoundException(id));
    }

    public BasketItem update(Long id, BasketItem payload) {
        BasketItem existing = findById(id);
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
