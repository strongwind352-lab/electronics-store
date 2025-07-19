package com.electronics.store.dto;

import lombok.Data;

@Data
public class BasketUpdateRequest {
  private Long productId;
  private int quantity;
}
