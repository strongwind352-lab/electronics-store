package com.electronics.store.service;

import com.electronics.store.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;

  public void addProductToBasket(Long productId, int quantity) {}
}
