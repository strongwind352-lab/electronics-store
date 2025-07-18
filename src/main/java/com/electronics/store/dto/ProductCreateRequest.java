package com.electronics.store.dto;

import com.electronics.store.model.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductCreateRequest {
  @NotBlank(message = "Product name must not be blank")
  private String name;

  @NotNull(message = "Product category must be specified")
  private ProductCategory category;

  @NotNull(message = "Price must be specified")
  @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than 0")
  private BigDecimal price;

  @Min(value = 0, message = "Product stock must be positive")
  private int stock;
}
