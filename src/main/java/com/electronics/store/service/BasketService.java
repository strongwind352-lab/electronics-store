package com.electronics.store.service;

import com.electronics.store.model.Basket;
import com.electronics.store.model.BasketItem;
import com.electronics.store.repository.BasketRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;
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
}
