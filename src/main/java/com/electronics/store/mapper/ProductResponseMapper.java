package com.electronics.store.mapper;

import com.electronics.store.dto.ProductResponse;
import com.electronics.store.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {

  @Mapping(source = "available", target = "available")
  ProductResponse toDto(Product product);
}
