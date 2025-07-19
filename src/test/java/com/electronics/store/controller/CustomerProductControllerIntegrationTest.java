package com.electronics.store.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerProductControllerIntegrationTest {
  @Autowired MockMvc mockMvc;

  @Autowired
  ProductRepository  productRepository;

    private Product laptop;
    private Product mouse;
    private Product keyboard;
    private Product book;
    private Product tablet;

    @BeforeEach
  void setUp() {
      productRepository.deleteAll();

      laptop = productRepository.save(new Product(null, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10));
      mouse = productRepository.save(new Product(null, "Wireless Mouse", ProductCategory.ELECTRONICS, BigDecimal.valueOf(25.00), 50));
      keyboard = productRepository.save(new Product(null, "Mechanical Keyboard", ProductCategory.ELECTRONICS, BigDecimal.valueOf(75.00), 0));
      book = productRepository.save(new Product(null, "Java Programming", ProductCategory.BOOKS, BigDecimal.valueOf(45.00), 100));
      tablet = productRepository.save(new Product(null, "Android Tablet", ProductCategory.ELECTRONICS, BigDecimal.valueOf(300.00), 15));
  }

    @Test
    @DisplayName("GET /customer/products - should return all products with default pagination with no filters applied - no login" )
    void getFilteredProducts_shouldReturnAllProductsWithDefaultPagination() throws Exception {
        mockMvc.perform(get("/customer/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.content[0].available").isBoolean());
    }

  @Test
  @DisplayName(
      "GET /customer/products?category=ELECTRONICS - should return all products with default pagination with category filters applied - no login")
  void getFilteredProducts_shouldFilterByCategory() throws Exception {
    mockMvc
        .perform(get("/customer/products").param("category", "ELECTRONICS"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements").value(4))
        .andExpect(jsonPath("$.content[0].available").isBoolean())
        .andExpect(
            jsonPath(
                "$.content[*].name",
                containsInAnyOrder(
                    "Laptop Pro", "Wireless Mouse", "Mechanical Keyboard", "Android Tablet")));
  }

  @Test
  void getFilteredProducts() {

  }
}
