package com.electronics.store.dto;

import com.electronics.store.model.ProductCategory;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductResponse {
  Long id;
  String name;
  ProductCategory category;
  BigDecimal price;
  int stock;
}
