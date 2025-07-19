package com.electronics.store.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.electronics.store.dto.BasketUpdateRequest;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.ProductRepository;
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
class CustomerBasketControllerIntegrationTest {
  private static final String CUSTOMER_USER_ID = "customer";
  @Autowired ObjectMapper objectMapper;
  @Autowired ProductRepository productRepository;
  Product laptop;
  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    laptop =
        productRepository.save(
            new Product(
                null, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10));
  }

  @Test
  @DisplayName("POST /customer/basket/add - should succeed and and decrement stock - CUSTOMER ROLE")
  @WithMockUser(username = CUSTOMER_USER_ID, roles = "CUSTOMER")
  void addProductToBasket_shouldSucceedAndDecrementStock() throws Exception {
    // Arrange
    BasketUpdateRequest addRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(2).build();

    // Act & Assert
    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].productId").value(laptop.getId()))
        .andExpect(jsonPath("$.items[0].quantity").value(2))
        .andExpect(jsonPath("$.userId").value(CUSTOMER_USER_ID));

    // Act & Assert 2
    mockMvc
        .perform(get("/admin/products", laptop.getId()).with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].stock").value(8));
  }

  @Test
  @DisplayName("POST /customer/basket/add - should return forbidden for non-customer role")
  @WithMockUser(roles = "ADMIN")
  void addProductToBasket_shouldReturnForbiddenForNonCustomer() throws Exception {
    // Arrange
    BasketUpdateRequest addRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(2).build();

    // Act & Assert
    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName(
      "POST /customer/basket/add - should return bad request for insufficient stock - CUSTOMER role")
  @WithMockUser(username = "customer", roles = "CUSTOMER")
  void addProductToBasket_shouldReturnBadRequestForInsufficientStock() throws Exception {
    // Arrange
    BasketUpdateRequest addRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(999).build();

    // Act & Assert 1 - POST to add product to basket
    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message")
                .value("Insufficient stock for product ID 48. Available : 10 - Requested : 999"));

    // Act @ Assert 2 - Verify that stock is unchanged
    mockMvc
        .perform(
            get("/admin/products/{productId}", laptop.getId()).with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock").value(10));
  }

  @Test
  @DisplayName(
      "POST /customer/basket/add - should return 404 not found for non-existent product - CUSTOMER role")
  @WithMockUser(username = "customer", roles = "CUSTOMER")
  void addProductToBasket_shouldReturnNotFoundForNonExistentProduct() throws Exception {
    // Arrange
    BasketUpdateRequest addRequest =
        BasketUpdateRequest.builder().productId(12345L).quantity(1).build();

    // Act & Assert 1 - POST to add non-existent product to basket
    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Product with ID 12345 not found."));
  }

  @Test
  @DisplayName(
      "POST /customer/basket/remove - should succeed and increment stock partially - CUSTOMER role")
  @WithMockUser(username = "customer", roles = "CUSTOMER")
  void removeProductFromBasket_shouldSucceedAndIncrementStockPartially() throws Exception {
    // Arrange
    BasketUpdateRequest basketUpdateRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(5).build();

    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketUpdateRequest)))
        .andExpect(status().isOk());
    // now we have 5 laptops in the basket

    // Arrange : now we call /customer/basket/remove and remove 2 laptops from the basket
    basketUpdateRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(2).build();

    // Act & Assert : verify that returned basket contains 3 laptops
    mockMvc
        .perform(
            post("/customer/basket/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketUpdateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].productId").value(laptop.getId()))
        .andExpect(jsonPath("$.items[0].quantity").value(3)); // 5 - 2 = 3

    // Act & Assert : verify that product stock is incremented by 2 since 2 laptops are removed
    // above
    mockMvc
        .perform(
            get("/admin/products/{productId}", laptop.getId()).with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock").value(7));
  }

  @Test
  @DisplayName("POST /customer/basket/remove - should return forbidden 403 for non customer")
  @WithMockUser(username = "admin", roles = "ADMIN")
  void removeProductFromBasket_shouldReturnForbiddenForNonCustomer() throws Exception {
    // Arrange
    BasketUpdateRequest basketUpdateRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(5).build();

    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketUpdateRequest))
                .with(user(CUSTOMER_USER_ID).roles("CUSTOMER")))
        .andExpect(status().isOk());
    // now we have 5 laptops in the basket

    // Arrange : now we call /customer/basket/remove and remove 2 laptops from the basket
    basketUpdateRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(2).build();

    // Act & Assert : verify that returned basket contains 3 laptops
    mockMvc
        .perform(
            post("/customer/basket/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketUpdateRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName(
      "POST /customer/basket/remove - should remove product entirely from basket and increment all stock - CUSTOMER role")
  @WithMockUser(username = CUSTOMER_USER_ID, roles = "CUSTOMER")
  void removeProductFromBasket_shouldSucceedAndIncrementStockEntirely() throws Exception {
    // Arrange
    BasketUpdateRequest basketUpdateRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(3).build();

    mockMvc
        .perform(
            post("/customer/basket/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketUpdateRequest)))
        .andExpect(status().isOk());
    // now we have 3 laptops in the basket

    // Arrange : now we call /customer/basket/remove and remove 3 laptops from the basket
    basketUpdateRequest =
        BasketUpdateRequest.builder().productId(laptop.getId()).quantity(3).build();

    // Act & Assert : verify that returned basket is empty
    mockMvc
        .perform(
            post("/customer/basket/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basketUpdateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isEmpty());

    // Act & Assert : verify that product stock is incremented fully
    // above
    mockMvc
        .perform(
            get("/admin/products/{productId}", laptop.getId()).with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock").value(10));
  }
}
