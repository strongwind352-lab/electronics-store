package com.electronics.store.dto;

import com.electronics.store.model.ProductCategory;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductResponse {
  Long id;
  String name;
  ProductCategory category;
  BigDecimal price;
  int stock;
  boolean available;
}
