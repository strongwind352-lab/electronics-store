package com.electronics.store.service;

import com.electronics.store.exception.ProductNotFoundException;
import com.electronics.store.model.Deal;
import com.electronics.store.repository.DealRepository;
import com.electronics.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DealService {
  private final DealRepository dealRepository;
  private final ProductRepository productRepository;

  public Deal createDeal(Deal deal) {
    Long productId = deal.getProductId();
    if (!productRepository.existsById(productId)) {
      throw new ProductNotFoundException(
          String.format("Product with id: %s does not exist", productId));
    }
    return dealRepository.save(deal);
  }
}
