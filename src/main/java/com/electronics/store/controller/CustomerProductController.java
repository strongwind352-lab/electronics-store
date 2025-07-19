package com.electronics.store.controller;

import com.electronics.store.dto.ProductCreateRequest;
import com.electronics.store.dto.ProductResponse;
import com.electronics.store.mapper.ProductMapper;
import com.electronics.store.mapper.ProductResponseMapper;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.service.ProductService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/products")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@Validated
public class CustomerProductController {
  private final ProductService productService;

  private final ProductResponseMapper productResponseMapper;
  private final ProductMapper productMapper;

  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getFilteredProducts(
          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) ProductCategory productCategory,
          @RequestParam(required = false) BigDecimal minPrice,
          @RequestParam(required = false) BigDecimal maxPrice,
          @RequestParam(required = false) Boolean available
          ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products = productService.filterProducts(productCategory,minPrice,maxPrice,available,pageable);
    Page<ProductResponse> productResponses = products.map(productResponseMapper::toDto);
    return new ResponseEntity<>(productResponses, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @Valid @RequestBody ProductCreateRequest request) {
    Product product = productMapper.toEntity(request);
    Product createdProduct = productService.createProduct(product);
    ProductResponse createdProductResponse = productResponseMapper.toDto(createdProduct);
    return new ResponseEntity<>(createdProductResponse, HttpStatus.CREATED);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
    productService.removeProduct(productId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
