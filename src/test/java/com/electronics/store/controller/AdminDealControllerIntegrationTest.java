package com.electronics.store.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.electronics.store.model.Deal;
import com.electronics.store.model.DealType;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminDealControllerIntegrationTest {
  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired ProductRepository productRepository;

  Product laptop;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    Product product =
        new Product(
            null, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.000), 10);
    laptop = productRepository.save(product);
  }

  @Test
  @DisplayName("createDeal - should return created deal")
  @WithMockUser(roles = "ADMIN")
  void createDeal_shouldReturnCreatedDeal() throws Exception {
    // Arrange data
    Deal newDeal = new Deal(null, laptop.getId(), DealType.BOGO50, LocalDateTime.now().plusDays(7));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/admin/deals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDeal)))
            .andReturn();
    mvcResult.getResponse().getContentAsString();
    mockMvc
        .perform(
            post("/admin/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDeal)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.productId").value(laptop.getId()))
        .andExpect(jsonPath("$.dealType").value(DealType.BOGO50.name()))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  @DisplayName("createDeal - should return forbidden for non - admin")
  @WithMockUser(roles = "DUMMY")
  void createDeal_shouldReturnForbiddenForNonAdmin() throws Exception {
    // Arrange data
    Deal newDeal = new Deal(null, laptop.getId(), DealType.BOGO50, LocalDateTime.now().plusDays(7));

    // Act and assert
    mockMvc
        .perform(
            post("/admin/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDeal)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName(
      "createDeal - should return not found if product to add deal to does not exist - admin")
  @WithMockUser(roles = "ADMIN")
  void createDeal_shouldReturnNotFoundIfProductDoesNotExist() throws Exception {
    // Arrange data
    Deal newDeal = new Deal(null, 777L, DealType.BOGO50, LocalDateTime.now().plusDays(7));

    // Act and assert
    mockMvc
        .perform(
            post("/admin/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDeal)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Product with id: 777 does not exist"));
  }

  @Test
  @DisplayName("createDeal - should return bad request for invalid deal request body - admin")
  @WithMockUser(roles = "ADMIN")
  void createDeal_shouldReturnBadRequestForInvalidDealRequestBody() throws Exception {
    // Arrange data
    Deal newDeal = new Deal(null, null, DealType.BOGO50, LocalDateTime.now().plusDays(7));

    // Act and assert
    mockMvc
        .perform(
            post("/admin/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDeal)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(
            jsonPath("$.message").value("productId: Please specify productId to add deal to"));
  }
}
