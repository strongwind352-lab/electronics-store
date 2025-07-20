package com.electronics.store.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.electronics.store.exception.InsufficientStockException;
import com.electronics.store.exception.ProductNotFoundException;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product laptop;
    private Product mouse;

    @BeforeEach
    void setUp() {
        laptop = new Product(1L, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
    mouse =
        new Product(
            2L, "Wireless Mouse", ProductCategory.ELECTRONICS, BigDecimal.valueOf(25.00), 50);
      }

    @Test
    void getAllProducts() {
      }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_shouldCreateProductSuccessfully() {
        // Arrange
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(laptop);

        // Act
        Product createdProduct = productService.createProduct(laptop);

        // Assert : Verify that product was successfully created
        assertNotNull(createdProduct);
        assertEquals("Laptop Pro", laptop.getName());
        verify(productRepository,times(1)).save(laptop);
      }

    @Test
    @DisplayName("Should remove product successfully")
    void removeProduct_shouldRemoveProductSuccessfully() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.removeProduct(1L);

        // Assert : Verify that product was successfully created
        verify(productRepository,times(1)).existsById(1L);
        verify(productRepository,times(1)).deleteById(1L);
    }

  @Test
  @DisplayName("Should throw ProductNotFoundException when removing non existent product")
  void removeProduct_shouldThrowProductNotFoundExceptionWhenRemovingNonExistentProduct() {
    // Arrange
    when(productRepository.existsById(1L)).thenReturn(false);

    // Act
    ProductNotFoundException productNotFoundException =
        assertThrows(ProductNotFoundException.class, () -> productService.removeProduct(1L));

    // Assert : Verify
    assertEquals("Product with ID 1 not found.", productNotFoundException.getMessage());
    verify(productRepository, times(1)).existsById(1L);
    verify(productRepository, never()).deleteById(1L);
  }

    @Test
    @DisplayName("Should return paginated products")
    void getAllProducts_shouldReturnPaginatedProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = new PageImpl<>(List.of(laptop,mouse), pageable, 2);
        when(productRepository.findAll(pageable)).thenReturn(products);

        // Act
        Page<Product> allProducts = productService.getAllProducts(pageable);

        // Assert : Verify
        assertNotNull(allProducts);
        assertEquals(2, allProducts.getTotalElements());
        assertEquals(laptop.getName(), allProducts.getContent().get(0).getName());
        assertEquals(mouse.getName(), allProducts.getContent().get(1).getName());
        verify(productRepository,times(1)).findAll(pageable);
    }

  @Test
  @DisplayName("Should decrement product stock")
  void decrementProductStock_shouldDecrementProductStock() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
    when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(laptop);

    // Act
    productService.decrementProductStock(laptop.getId(), 3);

    // Assert : Verify
    assertEquals(7, laptop.getStock());
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).save(laptop);
  }

  @Test
  @DisplayName(
      "Should throw InsufficientStockException when attempting to decrement stock beyond available quantity")
  void decrementProductStock_shouldThrowInsufficientStockException() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));

    // Act
    InsufficientStockException insufficientStockException =
        assertThrows(
            InsufficientStockException.class,
            () -> productService.decrementProductStock(laptop.getId(), 999));

    // Assert
    assertEquals(
        "Insufficient stock for product ID 1. Available : 10 - Requested : 999",
        insufficientStockException.getMessage());
    verify(productRepository, never()).save(laptop);
  }

  @Test
  @DisplayName(
      "Should throw ProductNotFoundException when attempting to decrement stock for a non-existent product")
  void decrementProductStock_shouldThrowProductNotFoundException() {
    // Arrange
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // Act
    ProductNotFoundException productNotFoundException =
        assertThrows(
            ProductNotFoundException.class, () -> productService.decrementProductStock(999L, 1));

    // Assert
    assertEquals("Product with ID 999 not found.", productNotFoundException.getMessage());
    verify(productRepository, times(1)).findById(999L);
    verify(productRepository, never()).save(ArgumentMatchers.any(Product.class));
  }

  @Test
  @DisplayName("Should successfully increment product stock and save the updated product")
  void incrementProductStock_shouldIncrementProductStockSuccessfully() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
    when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(laptop);

    // Act
    productService.incrementProductStock(laptop.getId(), 3);

    // Assert : Verify
    assertEquals(13, laptop.getStock());
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).save(laptop);
  }

  @Test
  @DisplayName(
      "Should throw ProductNotFoundException when attempting to increment stock for a non-existent product")
  void
      incrementProductStock_shouldThrowProductNotFoundExceptionWhenIncrementingStockForNonExistentProduct() {
    // Arrange
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // Act
    ProductNotFoundException productNotFoundException =
        assertThrows(
            ProductNotFoundException.class, () -> productService.incrementProductStock(999L, 1));

    // Assert
    assertEquals("Product with ID 999 not found.", productNotFoundException.getMessage());
    verify(productRepository, times(1)).findById(999L);
    verify(productRepository, never()).save(ArgumentMatchers.any(Product.class));
  }

  @Test
  @DisplayName("Concurrent stock decrement should be thread-safe and result in correct final stock")
  void decrementProductStock_concurrentStockDecrementShouldBeThreadSafe()
      throws InterruptedException {
    // Arrange
    int originalStock = 197;
    Product concurrentProduct =
        new Product(
            2L,
            "Head Phone",
            ProductCategory.ELECTRONICS,
            BigDecimal.valueOf(1200.00),
            originalStock);
      when(productRepository.findById(2L)).thenReturn(Optional.of(concurrentProduct));
    when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(concurrentProduct);

    // Act
    int numThreads = 16;
    int perThreadDecrement = 7;
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      executorService.submit(
          () -> {
            productService.decrementProductStock(2L, perThreadDecrement);
          });
    }
    executorService.shutdown();
    boolean result = executorService.awaitTermination(1000, TimeUnit.SECONDS);

    // Assert
    assertEquals(
        originalStock - numThreads * perThreadDecrement,
        concurrentProduct.getStock()); // 197 - 7*16 = 85
    verify(productRepository, times(numThreads)).findById(2L);
    verify(productRepository, times(numThreads)).save(concurrentProduct);
  }

    @Test
    void removeProduct() {
      }

    @Test
    void filterProducts() {
      }

    @Test
    void findProductById() {
      }

    @Test
    void decrementProductStock() {
      }

    @Test
    void incrementProductStock() {
      }
}