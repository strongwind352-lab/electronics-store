package com.electronics.store.controller;

import com.electronics.store.dto.ProductResponse;
import com.electronics.store.mapper.ProductMapper;
import com.electronics.store.mapper.ProductResponseMapper;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.service.ProductService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/products")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@Validated
public class CustomerBasketController {
  private final BasketService basketService;
  private final ProductService productService;

  private final ProductResponseMapper productResponseMapper;
  private final ProductMapper productMapper;

  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getFilteredProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) ProductCategory category,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(required = false) Boolean available) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products =
        productService.filterProducts(category, minPrice, maxPrice, available, pageable);
    Page<ProductResponse> productResponses = products.map(productResponseMapper::toDto);
    return new ResponseEntity<>(productResponses, HttpStatus.OK);
  }
}
