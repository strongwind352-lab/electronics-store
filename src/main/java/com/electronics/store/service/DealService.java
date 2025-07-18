package com.electronics.store.service;

import com.electronics.store.model.Deal;
import com.electronics.store.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DealService {
  private final DealRepository dealRepository;

  public Deal createDeal(Deal deal) {
    return dealRepository.save(deal);
  }
}
