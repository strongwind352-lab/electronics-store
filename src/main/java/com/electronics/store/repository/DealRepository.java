package com.electronics.store.repository;

import com.electronics.store.model.Deal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long>, JpaSpecificationExecutor<Deal> {
  //    List<Deal> productId(Long productId);

  Optional<Deal> findByProductId(Long id);
}
