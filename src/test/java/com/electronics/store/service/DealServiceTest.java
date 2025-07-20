package com.electronics.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.electronics.store.exception.ProductNotFoundException;
import com.electronics.store.model.Deal;
import com.electronics.store.model.DealType;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.DealRepository;
import com.electronics.store.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {
  @Mock private ProductRepository productRepository;

  @Mock private DealRepository dealRepository;

  @InjectMocks private DealService dealService;

  private Product laptop;

  @BeforeEach
  void setUp() {
    laptop =
        new Product(1L, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
  }

  @Test
  @DisplayName("Should create deal successfully for an existing product")
  void createDeal_shouldCreateDealSuccessfullyForAnExistingProduct() {
    // Arrange
    Deal deal = new Deal(1L, laptop.getId(), DealType.BOGO50, LocalDateTime.now().plusDays(7));
    when(productRepository.existsById(laptop.getId())).thenReturn(true);
    when(dealRepository.save(any(Deal.class))).thenReturn(deal);

    // Act
    Deal createdDeal = dealService.createDeal(deal);

    // Assert
    assertNotNull(createdDeal);
    assertEquals(DealType.BOGO50, deal.getDealType());
    verify(productRepository, times(1)).existsById(laptop.getId());
    verify(dealRepository, times(1)).save(any(Deal.class));
  }

  @Test
  @DisplayName(
      "Should throw ProductNotFoundException when adding a deal for a non-existent product")
  void createDeal_shouldThrowProductNotFoundExceptionWhenAddingDealForNonExistentProduct() {
    // Arrange
    Deal deal = new Deal(1L, 999L, DealType.BOGO50, LocalDateTime.now().plusDays(7));
    when(productRepository.existsById(999L)).thenReturn(false);

    // Act
    ProductNotFoundException productNotFoundException =
        assertThrows(ProductNotFoundException.class, () -> dealService.createDeal(deal));

    // Assert

    assertEquals("Product with id: 999 does not exist", productNotFoundException.getMessage());
    verify(productRepository, times(1)).existsById(999L);
    verify(dealRepository, never()).save(any(Deal.class));
  }

    @Test
    @DisplayName("Deal should be considered active if its expiration date is in the future")
    void dealShouldBeActiveIfExpirationDateInFuture() {
        Deal deal = new Deal(1L, laptop.getId(), DealType.BOGO50, LocalDateTime.now().plusDays(7));
        assertTrue(deal.isActive());
    }

    @Test
    @DisplayName("Deal should be considered active if its expiration date is null (no expiration)")
    void dealShouldBeActiveIfExpirationDateIsNull() {
        Deal deal = new Deal(1L, laptop.getId(), DealType.BOGO50, null);
        assertTrue(deal.isActive());
    }

    @Test
    @DisplayName("Deal should not be considered active if its expiration date is in the past")
    void dealShouldNotBeActiveIfExpirationDateInPast() {
        Deal deal = new Deal(1L, laptop.getId(), DealType.BOGO50, LocalDateTime.now().minusDays(1));
        assertFalse(deal.isActive());
    }
}
