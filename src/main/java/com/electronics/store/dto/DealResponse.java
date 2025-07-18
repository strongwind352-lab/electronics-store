package com.electronics.store.dto;

import com.electronics.store.model.DealType;

import java.time.LocalDateTime;

public class DealResponse {
  private Long productId;
  private DealType dealType;
  private LocalDateTime expirationDate;
}
