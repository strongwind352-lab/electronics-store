package com.electronics.store.service;

import com.electronics.store.exception.ProductNotFoundException;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;
  private final ConcurrentHashMap<Long, Object> productsLock = new ConcurrentHashMap<>();

  public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  public Product createProduct(Product product) {
    return productRepository.save(product);
  }

  public void removeProduct(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new ProductNotFoundException(String.format("Product with ID %s not found.", productId));
    }
    productRepository.deleteById(productId);
  }

  public Page<Product> filterProducts(
      ProductCategory productCategory,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Boolean available,
      Pageable pageable) {
    Specification<Product> spec = (root, query, criteriaBuilder) -> null;
    if (productCategory != null) {
      spec =
          spec.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("category"), productCategory));
    }
    if (minPrice != null && maxPrice != null) {
      spec =
          spec.and(
              ((root, query, criteriaBuilder) ->
                  criteriaBuilder.between(root.get("price"), minPrice, maxPrice)));
    } else if (minPrice != null) {
      spec =
          spec.and(
              ((root, query, criteriaBuilder) ->
                  criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)));
    } else if (maxPrice != null) {
      spec =
          spec.and(
              ((root, query, criteriaBuilder) ->
                  criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice)));
    }

    if (available != null && available) {
      spec =
          spec.and(
              ((root, query, criteriaBuilder) ->
                  criteriaBuilder.greaterThan(root.get("stock"), 0)));
    }
    return productRepository.findAll(spec, pageable);
  }

  public Product findProductById(Long productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () ->
                new ProductNotFoundException(
                    String.format("Product with ID %s not found.", productId)));
  }

  public void decrementProductStock(Long productId, int quantity) {
    Product product = findProductById(productId);
    productsLock.putIfAbsent(productId, new Object());
    synchronized (productsLock.get(productId)) {
      product.decrementStock(quantity);
      productRepository.save(product);
    }
  }

  public void incrementProductStock(Long productId, int quantity) {
    Product product = findProductById(productId);
    synchronized (productsLock.get(productId)) {
      product.incrementStock(quantity);
      productRepository.save(product);
    }
  }
}
