package com.electronics.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BasketRepository extends JpaRepository<Basket,Long> , JpaSpecificationExecutor<Basket> {}
