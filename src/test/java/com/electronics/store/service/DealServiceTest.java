package com.electronics.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    Deal deal = new Deal(1L, laptop.getId(), DealType.BOGO50, LocalDateTime.now());
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
}
