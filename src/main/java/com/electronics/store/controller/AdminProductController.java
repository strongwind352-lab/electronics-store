package com.electronics.store.controller;

import com.electronics.store.dto.ProductCreateRequest;
import com.electronics.store.dto.ProductResponse;
import com.electronics.store.mapper.ProductMapper;
import com.electronics.store.mapper.ProductResponseMapper;
import com.electronics.store.model.Product;
import com.electronics.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
  private final ProductService productService;

  private final ProductResponseMapper productResponseMapper;
  private final ProductMapper productMapper;

  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getAllProducts(
          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products = productService.getAllProducts(pageable);
    Page<ProductResponse> productResponses = products.map(productResponseMapper::toDto);
    return new ResponseEntity<>(productResponses, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
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
