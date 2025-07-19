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
import org.springframework.security.test.context.support.WithMockUser;
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
  @DisplayName(
      "GET /customer/products?minPrice=50&maxPrice=100 - should return all products with price between minPrice and maxPrice - no login")
  void getFilteredProducts_shouldFilterByPriceRange() throws Exception {

        // Act & Assert
    mockMvc
        .perform(get("/customer/products").param("minPrice", "50").param("maxPrice", "100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].available").isBoolean())
        .andExpect(jsonPath("$.content[0].name").value("Mechanical Keyboard"))
        .andExpect(jsonPath("$.content[0].category").value("ELECTRONICS"))
        .andExpect(jsonPath("$.content[0].available").isBoolean());
  }

  @Test
  @DisplayName("GET /customer/products?available=true - no login")
  void getFilteredProducts_shouldFilterByAvailability() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/customer/products").param("available", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.totalElements").value(4))
        .andExpect(jsonPath("$.content[0].available").isBoolean())
        .andExpect(
            jsonPath(
                "$.content[*].name",
                containsInAnyOrder(
                    "Laptop Pro", "Wireless Mouse", "Java Programming", "Android Tablet")));
  }

  @Test
  @DisplayName(
      "GET /customer/products?category=ELECTRONICS&available=true - Should filter by category and availability - no login")
  void getFilteredProducts_shouldFilterByCategoryAndAvailability() throws Exception {

    // Act & Assert
    mockMvc
        .perform(
            get("/customer/products").param("available", "true").param("category", "ELECTRONICS"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(3)))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content[0].available").value(true))
        .andExpect(
            jsonPath(
                "$.content[*].name",
                containsInAnyOrder("Laptop Pro", "Wireless Mouse", "Android Tablet")));
  }

  @Test
  @DisplayName(
      "GET /customer/products?category=ELECTRONICS&available=true&minPrice=100&maxPrice=1500 - Should filter by all criteria - no login")
  void getFilteredProducts_shouldFilterByByAllCriteria() throws Exception {

    // Act & Assert
    mockMvc
        .perform(
            get("/customer/products")
                .param("available", "true")
                .param("category", "ELECTRONICS")
                .param("minPrice", "100")
                .param("maxPrice", "1500"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].available").value(true))
        .andExpect(
            jsonPath("$.content[*].name", containsInAnyOrder("Laptop Pro", "Android Tablet")));
  }

  @Test
  @DisplayName("GET /customer/products?size=2&oage=1 - Should paginate with no filter - no login")
  void getFilteredProducts_shouldPaginate() throws Exception {

    // Act & Assert
    mockMvc
        .perform(get("/customer/products").param("page", "1").param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(5))
        .andExpect(jsonPath("$.totalPages").value(3))
        .andExpect(jsonPath("$.content[0].available").isBoolean())
        .andExpect(
            jsonPath(
                "$.content[*].name",
                containsInAnyOrder("Mechanical Keyboard", "Java Programming")));
  }

  @Test
  @DisplayName("GET /customer/products?size=2&oage=1 - Should return empty if no products match filter - CUSTOMER role")
  @WithMockUser(roles = "CUSTOMER")
  void getFilteredProducts_shouldReturnEmptyIfNoProductsMatchFilter() throws Exception {

    // Act & Assert
    mockMvc
            .perform(get("/customer/products")
                    .param("category", "CLOTHING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)))
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.totalPages").value(0));
  }

  @Test
  void getFilteredProducts() {

  }
}
