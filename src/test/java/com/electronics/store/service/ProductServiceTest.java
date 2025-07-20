package com.electronics.store.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product laptop;

    @BeforeEach
    void setUp() {
        laptop = new Product(1L, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
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