package com.electronics.store.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class AdminProductController {
    public void getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
    }

}
