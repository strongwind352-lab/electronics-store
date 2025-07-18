package com.electronics.store.dto;

import com.electronics.store.model.DealType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DealResponse {
  Long id;
  Long productId;
  DealType dealType;
  LocalDateTime expirationDate;
}
