package com.electronics.store.controller;

import com.electronics.store.dto.DealResponse;
import com.electronics.store.mapper.DealResponseMapper;
import com.electronics.store.model.Deal;
import com.electronics.store.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/deals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDealController {
  private final DealService dealService;
  private final DealResponseMapper dealResponseMapper;

  @PostMapping
  public ResponseEntity<DealResponse> createDeal(@Valid @RequestBody Deal deal) {
    Deal createdDeal = dealService.createDeal(deal);
    DealResponse dealResponse = dealResponseMapper.toDto(createdDeal);
    return new ResponseEntity<>(dealResponse, HttpStatus.CREATED);
  }
}
