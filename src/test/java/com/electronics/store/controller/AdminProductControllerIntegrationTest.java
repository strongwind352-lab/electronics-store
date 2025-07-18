package com.electronics.store.controller;

import static com.electronics.store.model.ProductCategory.ELECTRONICS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.electronics.store.dto.ProductCreateRequest;
import com.electronics.store.model.Product;
import com.electronics.store.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
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

  @Autowired ProductRepository productRepository;

  private Product laptop;
  private Product mouse;
  private Product keyboard;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    laptop = new Product(null, "Laptop Pro", ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
    mouse = new Product(null, "Wireless Mouse", ELECTRONICS, BigDecimal.valueOf(25.00), 50);
    keyboard = new Product(null, "Mechanical Keyboard", ELECTRONICS, BigDecimal.valueOf(75.00), 20);
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
    invalidProductCreateRequest.setCategory(ELECTRONICS);
    invalidProductCreateRequest.setPrice(BigDecimal.valueOf(-20));
    invalidProductCreateRequest.setStock(-5);

    // Act and Assert
    mockMvc
        .perform(
            post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductCreateRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message", containsString("name: Product name must not be blank")))
        .andExpect(jsonPath("$.message", containsString("price: Product price must be greater than 0")))
        .andExpect(jsonPath("$.message", containsString("Product stock must be positive")));
  }

  @Test
  @DisplayName("DELETE /admin/products/{productId} - Should return no content 204")
  @WithMockUser(roles = "ADMIN")
  void deleteProduct_shouldReturnNoContent() throws Exception {
    // Arrange
    Product savedProduct = productRepository.save(laptop);

    // Act and assert
    mockMvc
        .perform(delete("/admin/products/{productId}", savedProduct.getId()))
        .andExpect(status().isNoContent());

    // verify that database is empty since one last element is deleted
    mockMvc
        .perform(
            get("/admin/products")
                .param("page", "0")
                .param("size", "10")
                .with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  @DisplayName(
      "DELETE /admin/products/{productId} - Should return 403 Forbidden if user is not an admin")
  @WithMockUser(roles = "DUMMY")
  void deleteProduct_shouldReturnForbiddenForNonAdmin() throws Exception {
    Product savedProduct = productRepository.save(laptop);
    mockMvc
        .perform(delete("/admin/products/{productId}", savedProduct.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("DELETE /admin/products/{productId} - Should return 404 Not Found for a non-existent product - ADMIN")
  @WithMockUser(roles = "ADMIN")
  void deleteProduct_shouldReturnNotFoundForNonExistentProduct() throws Exception {
    // Act & Assert: Perform DELETE request for a non-existent ID
    mockMvc.perform(delete("/admin/products/{productId}", 999L))
            .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
            .andExpect(jsonPath("$.message").value("Product with ID 999 not found."));
  }

  @Test
  @DisplayName("GET /admin/products/ should return paginated products with admin role")
  @WithMockUser(roles = "ADMIN")
  void getAllProducts_shouldReturnPaginatedProducts() throws Exception {
    // Arrange
    productRepository.saveAll(List.of(laptop, mouse, keyboard));

    // Act - Assert - 1
    mockMvc
        .perform(
            get("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.totalPages").value(3))
        .andExpect(jsonPath("$.content[0].id").isNumber())
        .andExpect(jsonPath("$.content[0].name").value("Laptop Pro"))
        .andExpect(jsonPath("$.content[0].category").value(ELECTRONICS.toString()))
        .andExpect(jsonPath("$.content[0].price").value(BigDecimal.valueOf(1200.00)))
        .andExpect(jsonPath("$.content[0].stock").value(10));

    // Act - Assert - 2
    mockMvc
        .perform(
            get("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.content[0].id").isNumber())
        .andExpect(jsonPath("$.content[0].name").value("Laptop Pro"))
        .andExpect(jsonPath("$.content[0].category").value(ELECTRONICS.toString()))
        .andExpect(jsonPath("$.content[0].price").value(BigDecimal.valueOf(1200.00)))
        .andExpect(jsonPath("$.content[0].stock").value(10))
        .andExpect(jsonPath("$.content[1].id").isNumber())
        .andExpect(jsonPath("$.content[1].name").value("Wireless Mouse"))
        .andExpect(jsonPath("$.content[1].category").value(ELECTRONICS.toString()))
        .andExpect(jsonPath("$.content[1].price").value(BigDecimal.valueOf(25.00)))
        .andExpect(jsonPath("$.content[1].stock").value(50));
  }

  @Test
  @DisplayName("GET /admin/products - Should return 403 Forbidden for non -admin user")
  @WithMockUser(roles = "DUMMY")
  void getAllProducts_shouldReturnForbiddenForNonAdmin() throws Exception {
    mockMvc.perform(get("/admin/products")).andExpect(status().isForbidden());
  }
}
