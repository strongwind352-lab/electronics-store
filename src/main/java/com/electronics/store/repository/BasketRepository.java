package com.electronics.store.repository;

import com.electronics.store.model.Basket;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository
    extends JpaRepository<Basket, Long>, JpaSpecificationExecutor<Basket> {
  Optional<Basket> findByUserId(String userId);
}
