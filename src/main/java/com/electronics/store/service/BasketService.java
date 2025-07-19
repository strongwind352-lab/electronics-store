package com.electronics.store.service;

import com.electronics.store.model.Basket;
import com.electronics.store.model.BasketItem;
import com.electronics.store.model.Deal;
import com.electronics.store.model.DealType;
import com.electronics.store.model.Product;
import com.electronics.store.model.Receipt;
import com.electronics.store.model.ReceiptItem;
import com.electronics.store.repository.BasketRepository;
import com.electronics.store.repository.DealRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;
  private final DealRepository dealRepository;
  private final ProductService productService;

  public Basket addProductToBasket(Long productId, int quantity) {
    Basket basket = getOrUpdateBasket();
    productService.decrementProductStock(productId, quantity);
    Optional<BasketItem> existingBasket =
        basket.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    if (existingBasket.isPresent()) {
      existingBasket.get().setQuantity(existingBasket.get().getQuantity() + quantity);
    } else {
      basket.getItems().add(new BasketItem(productId, quantity));
    }
    return basketRepository.save(basket);
  }

  private Basket getOrUpdateBasket() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("Authentication required");
    }
    String userId = authentication.getName();

    return basketRepository
        .findByUserId(userId)
        .orElseGet(() -> basketRepository.save(new Basket(userId)));
  }

  public Basket removeProductFromBasket(Long productId, int quantity) {
    Basket basket = getOrUpdateBasket();
    productService.incrementProductStock(productId, quantity);
    Optional<BasketItem> existingBasketItem =
        basket.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    if (existingBasketItem.isPresent()) {
      if (existingBasketItem.get().getQuantity() <= quantity) {
        basket.getItems().remove(existingBasketItem.get());
      } else {
        existingBasketItem.get().setQuantity(existingBasketItem.get().getQuantity() - quantity);
      }
    }
    return basketRepository.save(basket);
  }

  public Receipt calculateReceipt() {
    Basket basket = getOrUpdateBasket();
    List<ReceiptItem> receiptItems = new ArrayList<>();
    List<String> dealsApplied = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;
    for (int receiptItemNumber = 0;
        receiptItemNumber < basket.getItems().size();
        receiptItemNumber++) {
      BasketItem item = basket.getItems().get(receiptItemNumber);
      Product product = productService.findProductById(item.getProductId());
      ReceiptItem.ReceiptItemBuilder receiptItemBuilder =
          ReceiptItem.builder()
              .productId(product.getId())
              .productName(product.getName())
              .category(product.getCategory())
              .originalPrice(product.getPrice())
              .quantity(item.getQuantity())
              .priceAfterDeal(product.getPrice());
      if (receiptItemNumber % 2 == 1) {
        Optional<List<Deal>> dealsOptional = dealRepository.findByProductId(product.getId());
        if (dealsOptional.isPresent()) {
          List<Deal> deals = dealsOptional.get();
          Deal bogo50Deal =
              deals.stream()
                  .filter(deal -> DealType.BOGO50.equals(deal.getDealType()) && deal.isActive())
                  .findFirst()
                  .orElse(null);
          if (bogo50Deal != null) {
            receiptItemBuilder.priceAfterDeal(product.getPrice().multiply(BigDecimal.valueOf(0.5)));
            receiptItemBuilder.dealApplied(DealType.BOGO50.toString());
            dealsApplied.add(DealType.BOGO50.toString());
          }
        }
      }
      ReceiptItem receiptItem = receiptItemBuilder.build();
      receiptItems.add(receiptItem);
      totalPrice =
          totalPrice.add(
              receiptItem
                  .getPriceAfterDeal()
                  .multiply(BigDecimal.valueOf(receiptItem.getQuantity())));
    }

    return Receipt.builder()
        .items(receiptItems)
        .dealsApplied(dealsApplied)
        .totalPrice(totalPrice)
        .build();
  }
}
