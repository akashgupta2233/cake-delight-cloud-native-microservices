package com.cakedelight.order.service;

import com.cakedelight.order.entity.BasketItem;
import com.cakedelight.order.repository.BasketItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketItemRepository basketItemRepository;

    @InjectMocks
    private BasketService basketService;

    @Test
    void createIncrementsQuantityWhenCakeAlreadyExists() {
        BasketItem existing = basketItem(1L, 10L, "Chocolate Cake", 2);
        BasketItem incoming = basketItem(null, 10L, "Chocolate Cake", 1);
        when(basketItemRepository.findByCakeId(10L)).thenReturn(Optional.of(existing));
        when(basketItemRepository.save(existing)).thenReturn(existing);

        BasketItem saved = basketService.create(incoming);

        assertThat(saved).isSameAs(existing);
        assertThat(existing.getQuantity()).isEqualTo(3);
        verify(basketItemRepository).save(existing);
    }

    @Test
    void createAddsNewRowWhenCakeDoesNotExist() {
        BasketItem incoming = basketItem(99L, 10L, "Chocolate Cake", 1);
        when(basketItemRepository.findByCakeId(10L)).thenReturn(Optional.empty());
        when(basketItemRepository.save(incoming)).thenReturn(incoming);

        BasketItem saved = basketService.create(incoming);

        assertThat(saved).isSameAs(incoming);
        assertThat(incoming.getId()).isNull();
        verify(basketItemRepository).save(incoming);
    }

    @Test
    void findAllUsesAscendingIdOrder() {
        List<BasketItem> expected = List.of(
                basketItem(1L, 10L, "Chocolate Cake", 1),
                basketItem(2L, 11L, "Vanilla Cake", 1)
        );
        when(basketItemRepository.findAllByOrderByIdAsc()).thenReturn(expected);

        List<BasketItem> actual = basketService.findAll();

        assertThat(actual).containsExactlyElementsOf(expected);
        verify(basketItemRepository).findAllByOrderByIdAsc();
    }

    @Test
    void updateChangesOnlyQuantity() {
        BasketItem existing = basketItem(1L, 10L, "Chocolate Cake", 1);
        BasketItem payload = basketItem(null, 99L, "Different Cake", 4);
        when(basketItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(basketItemRepository.save(existing)).thenReturn(existing);

        BasketItem saved = basketService.update(1L, payload);

        assertThat(saved.getQuantity()).isEqualTo(4);
        assertThat(saved.getCakeId()).isEqualTo(10L);
        assertThat(saved.getCakeName()).isEqualTo("Chocolate Cake");
        verify(basketItemRepository).save(existing);
    }

    private BasketItem basketItem(Long id, Long cakeId, String cakeName, int quantity) {
        return BasketItem.builder()
                .id(id)
                .cakeId(cakeId)
                .cakeName(cakeName)
                .price(BigDecimal.TEN)
                .quantity(quantity)
                .build();
    }
}
