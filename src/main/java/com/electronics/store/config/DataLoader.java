package com.electronics.store.config;

import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.service.ProductService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
  private final ProductService productService;

  @Override
  public void run(String... args) throws Exception {
    loadProducts();
  }

  private void loadProducts() {
    System.out.println("Loading dummy products...");

    Product laptop =
        new Product(
            null, "Laptop Pro X1", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
    Product mouse =
        new Product(
            null, "Gaming Mouse G502", ProductCategory.ELECTRONICS, BigDecimal.valueOf(75.00), 50);
    Product keyboard =
        new Product(
            null,
            "Mechanical Keyboard K95",
            ProductCategory.ELECTRONICS,
            BigDecimal.valueOf(150.00),
            20);
    Product monitor =
        new Product(
            null,
            "Ultrawide Monitor 34",
            ProductCategory.ELECTRONICS,
            BigDecimal.valueOf(600.00),
            5);
    Product headset =
        new Product(
            null,
            "Wireless Headset H7",
            ProductCategory.ELECTRONICS,
            BigDecimal.valueOf(100.00),
            30);
    Product javaBook =
        new Product(null, "Effective Java", ProductCategory.BOOKS, BigDecimal.valueOf(45.00), 100);
    Product tShirt =
        new Product(
            null, "Spring Boot T-Shirt", ProductCategory.CLOTHING, BigDecimal.valueOf(25.00), 200);
    Product smartSpeaker =
        new Product(
            null,
            "Smart Speaker Echo",
            ProductCategory.HOME_APPLIANCES,
            BigDecimal.valueOf(99.99),
            15);
    Product outOfStockItem =
        new Product(null, "Rare Collectible", ProductCategory.TOYS, BigDecimal.valueOf(5000.00), 0);

    productService.createProduct(laptop);
    productService.createProduct(mouse);
    productService.createProduct(keyboard);
    productService.createProduct(monitor);
    productService.createProduct(headset);
    productService.createProduct(javaBook);
    productService.createProduct(tShirt);
    productService.createProduct(smartSpeaker);
    productService.createProduct(outOfStockItem);

    System.out.println("Dummy products loaded.");
  }
}
