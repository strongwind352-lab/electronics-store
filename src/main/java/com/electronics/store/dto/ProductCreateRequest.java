package com.electronics.store.dto;

import com.electronics.store.model.ProductCategory;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductCreateRequest {
  private String name;
  private ProductCategory category;
  private BigDecimal price;
  private int stock;
}
