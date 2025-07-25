package com.electronics.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Please specify productId to add deal to")
  private Long productId;

  @Enumerated(EnumType.STRING)
  private DealType dealType;

  private LocalDateTime expirationDate;

  public boolean isActive() {
    // expirationDate == null means deal never expires
    return expirationDate == null || expirationDate.isAfter(LocalDateTime.now());
  }
}
