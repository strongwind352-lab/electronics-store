package com.electronics.store.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Receipt {
  List<ReceiptItem> items;
  List<String> dealsApplied;
  BigDecimal totalPrice;
}
