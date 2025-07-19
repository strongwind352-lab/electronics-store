package com.electronics.store.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Basket {
  @Id @GeneratedValue private Long id;

  private String userId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "basket_items", joinColumns = @JoinColumn(name = "basket_id"))
  private List<BasketItem> items;

  public Basket(String userId) {
    this.userId = userId;
    items = new ArrayList<>();
  }
}
