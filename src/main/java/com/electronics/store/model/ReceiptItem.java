package com.electronics.store.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReceiptItem {
    Long productId;
    String productName;
    ProductCategory category;
    BigDecimal originalPrice;
    int quantity;
    BigDecimal priceAfterDeal;
    String dealApplied;
}
