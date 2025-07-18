package com.electronics.store.controller;

import com.electronics.store.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminDealController {
  private final DealService dealService;
}
