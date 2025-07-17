package com.electronics.store.config;

import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.service.ProductService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
  private final ProductService productService;

  @Override
  public void run(String... args) throws Exception {
    loadProducts();
  }

  private void loadProducts() {
    List<Product> productsToLoad = new ArrayList<>();

    // Electronics
    productsToLoad.add(new Product(null, "Laptop Pro X1", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10));
    productsToLoad.add(new Product(null, "Gaming Mouse G502", ProductCategory.ELECTRONICS, BigDecimal.valueOf(75.00), 50));
    productsToLoad.add(new Product(null, "Mechanical Keyboard K95", ProductCategory.ELECTRONICS, BigDecimal.valueOf(150.00), 20));
    productsToLoad.add(new Product(null, "Ultrawide Monitor 34", ProductCategory.ELECTRONICS, BigDecimal.valueOf(600.00), 5));
    productsToLoad.add(new Product(null, "Wireless Headset H7", ProductCategory.ELECTRONICS, BigDecimal.valueOf(100.00), 30));
    productsToLoad.add(new Product(null, "Smartphone Galaxy Z", ProductCategory.ELECTRONICS, BigDecimal.valueOf(999.99), 12));
    productsToLoad.add(new Product(null, "Smartwatch Series 7", ProductCategory.ELECTRONICS, BigDecimal.valueOf(399.00), 25));
    productsToLoad.add(new Product(null, "Drone Phantom 4", ProductCategory.ELECTRONICS, BigDecimal.valueOf(850.00), 3));
    productsToLoad.add(new Product(null, "VR Headset Oculus", ProductCategory.ELECTRONICS, BigDecimal.valueOf(299.00), 8));
    productsToLoad.add(new Product(null, "Portable SSD 1TB", ProductCategory.ELECTRONICS, BigDecimal.valueOf(120.00), 40));
    productsToLoad.add(new Product(null, "E-Reader Kindle Paperwhite", ProductCategory.ELECTRONICS, BigDecimal.valueOf(130.00), 60));
    productsToLoad.add(new Product(null, "Action Camera GoPro 10", ProductCategory.ELECTRONICS, BigDecimal.valueOf(350.00), 18));
    productsToLoad.add(new Product(null, "Smart TV 65-inch", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1500.00), 7));
    productsToLoad.add(new Product(null, "Soundbar Dolby Atmos", ProductCategory.ELECTRONICS, BigDecimal.valueOf(250.00), 22));
    productsToLoad.add(new Product(null, "Gaming Console PS5", ProductCategory.ELECTRONICS, BigDecimal.valueOf(499.99), 4));
    productsToLoad.add(new Product(null, "Webcam 1080p", ProductCategory.ELECTRONICS, BigDecimal.valueOf(49.99), 70));
    productsToLoad.add(new Product(null, "Router Wi-Fi 6", ProductCategory.ELECTRONICS, BigDecimal.valueOf(89.00), 35));
    productsToLoad.add(new Product(null, "Noise Cancelling Headphones", ProductCategory.ELECTRONICS, BigDecimal.valueOf(200.00), 15));
    productsToLoad.add(new Product(null, "Graphics Card RTX 4080", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1000.00), 2));
    productsToLoad.add(new Product(null, "Desktop PC Gaming", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1800.00), 6));
    productsToLoad.add(new Product(null, "Limited Edition Gadget", ProductCategory.ELECTRONICS, BigDecimal.valueOf(2500.00), 1));
    productsToLoad.add(new Product(null, "Popular Item X", ProductCategory.ELECTRONICS, BigDecimal.valueOf(100.00), 200));

    // Books
    productsToLoad.add(new Product(null, "Effective Java", ProductCategory.BOOKS, BigDecimal.valueOf(45.00), 100));
    productsToLoad.add(new Product(null, "Clean Code", ProductCategory.BOOKS, BigDecimal.valueOf(38.50), 80));
    productsToLoad.add(new Product(null, "The Pragmatic Programmer", ProductCategory.BOOKS, BigDecimal.valueOf(55.00), 70));
    productsToLoad.add(new Product(null, "Design Patterns", ProductCategory.BOOKS, BigDecimal.valueOf(60.00), 50));
    productsToLoad.add(new Product(null, "The Lord of the Rings", ProductCategory.BOOKS, BigDecimal.valueOf(25.00), 150));
    productsToLoad.add(new Product(null, "Dune", ProductCategory.BOOKS, BigDecimal.valueOf(18.00), 120));
    productsToLoad.add(new Product(null, "Clearance Item B", ProductCategory.BOOKS, BigDecimal.valueOf(2.50), 1000));

    // Clothing
    productsToLoad.add(new Product(null, "Spring Boot T-Shirt", ProductCategory.CLOTHING, BigDecimal.valueOf(25.00), 200));
    productsToLoad.add(new Product(null, "Java Developer Hoodie", ProductCategory.CLOTHING, BigDecimal.valueOf(40.00), 120));
    productsToLoad.add(new Product(null, "Tech Enthusiast Cap", ProductCategory.CLOTHING, BigDecimal.valueOf(15.00), 300));
    productsToLoad.add(new Product(null, "Smart Casual Shirt", ProductCategory.CLOTHING, BigDecimal.valueOf(35.00), 180));
    productsToLoad.add(new Product(null, "Clearance Item A", ProductCategory.CLOTHING, BigDecimal.valueOf(5.00), 500));

    // Home Appliances
    productsToLoad.add(new Product(null, "Smart Speaker Echo", ProductCategory.HOME_APPLIANCES, BigDecimal.valueOf(99.99), 15));
    productsToLoad.add(new Product(null, "Robot Vacuum Cleaner", ProductCategory.HOME_APPLIANCES, BigDecimal.valueOf(250.00), 10));
    productsToLoad.add(new Product(null, "Smart Coffee Maker", ProductCategory.HOME_APPLIANCES, BigDecimal.valueOf(79.00), 20));
    productsToLoad.add(new Product(null, "Air Fryer XL", ProductCategory.HOME_APPLIANCES, BigDecimal.valueOf(120.00), 25));
    productsToLoad.add(new Product(null, "Seasonal Decor", ProductCategory.HOME_APPLIANCES, BigDecimal.valueOf(30.00), 0));
    productsToLoad.add(new Product(null, "Popular Item Y", ProductCategory.HOME_APPLIANCES, BigDecimal.valueOf(50.00), 150));

    // Sports
    productsToLoad.add(new Product(null, "Smart Fitness Tracker", ProductCategory.SPORTS, BigDecimal.valueOf(80.00), 60));
    productsToLoad.add(new Product(null, "Electric Scooter", ProductCategory.SPORTS, BigDecimal.valueOf(400.00), 8));

    // Toys
    productsToLoad.add(new Product(null, "Programmable Robot Toy", ProductCategory.TOYS, BigDecimal.valueOf(120.00), 30));
    productsToLoad.add(new Product(null, "RC Car High Speed", ProductCategory.TOYS, BigDecimal.valueOf(65.00), 45));
    productsToLoad.add(new Product(null, "Rare Collectible", ProductCategory.TOYS, BigDecimal.valueOf(5000.00), 0));

    // Food (just for category diversity)
    productsToLoad.add(new Product(null, "Gourmet Coffee Beans", ProductCategory.FOOD, BigDecimal.valueOf(18.00), 500));
    productsToLoad.add(new Product(null, "Organic Snack Box", ProductCategory.FOOD, BigDecimal.valueOf(30.00), 300));

    productsToLoad.forEach(productService::createProduct);

    log.info("Dummy products loaded: {} items.", productsToLoad.size());

  }
}
