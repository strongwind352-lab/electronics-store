package com.electronics.store.controller;

import com.electronics.store.dto.BasketUpdateRequest;
import com.electronics.store.service.BasketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/basket")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Validated
public class CustomerBasketController {
  private final BasketService basketService;

  @PostMapping
  public ResponseEntity<Void> addProductToBasket(@Valid @RequestBody BasketUpdateRequest request) {
    basketService.addProductToBasket(request.getProductId(), request.getQuantity());
    return ResponseEntity.ok().build();
  }
}
