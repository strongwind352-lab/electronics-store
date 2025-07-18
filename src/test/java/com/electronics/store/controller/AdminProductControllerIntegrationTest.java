package com.electronics.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.electronics.store.dto.ProductCreateRequest;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminProductControllerIntegrationTest {
  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  private Product laptop;

  @BeforeEach
  void setUp() {
    laptop =
        new Product(
            null, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
  }

  @Test
  void getAllProducts() {}

  @Test
  @DisplayName(
      "POST /admin/products - Should return created product and 201 created status code (ADMIN role)")
  @WithMockUser(roles = "ADMIN")
  void createProduct_shouldReturnCreatedProduct() throws Exception {
    // Arrange
    ProductCreateRequest productCreateRequest = new ProductCreateRequest();
    productCreateRequest.setName(laptop.getName());
    productCreateRequest.setCategory(laptop.getCategory());
    productCreateRequest.setPrice(laptop.getPrice());
    productCreateRequest.setStock(laptop.getStock());

    // Act and Assert
    mockMvc
        .perform(
            post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Laptop Pro"))
        .andExpect(jsonPath("$.category").value("ELECTRONICS"))
        .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(1200.00)))
        .andExpect(jsonPath("$.stock").value(BigDecimal.valueOf(10)));
  }

  @Test
  @DisplayName(
      "POST /admin/products - Should return 403 Forbidden for non-admin user")
  @WithMockUser(roles = "DUMMY")
  void createProduct_shouldReturnForbiddenForNonAdmin() throws Exception {
    // Arrange
    ProductCreateRequest productCreateRequest = new ProductCreateRequest();
    productCreateRequest.setName(laptop.getName());
    productCreateRequest.setCategory(laptop.getCategory());
    productCreateRequest.setPrice(laptop.getPrice());
    productCreateRequest.setStock(laptop.getStock());

    // Act and Assert
    mockMvc
        .perform(
            post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName(
      "POST /admin/products - Should return bad request for invalid request body data - ADMIN")
  @WithMockUser(roles = "ADMIN")
  void createProduct_shouldReturnBadRequestForInvalidRequestBodyData() throws Exception {
    // Arrange
    ProductCreateRequest invalidProductCreateRequest = new ProductCreateRequest();
    invalidProductCreateRequest.setName("");
    invalidProductCreateRequest.setCategory(ProductCategory.ELECTRONICS);
    invalidProductCreateRequest.setPrice(BigDecimal.valueOf(-20));
    invalidProductCreateRequest.setStock(-5);

    // Act and Assert
    mockMvc
        .perform(
            post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductCreateRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteProduct() {}
}
