package com.electronics.store.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasketUpdateRequest {
  private Long productId;
  private int quantity;
}
