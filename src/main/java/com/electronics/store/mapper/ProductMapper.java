package com.electronics.store.mapper;

import com.electronics.store.dto.ProductCreateRequest;
import com.electronics.store.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  @Mapping(target = "id", ignore = true)
  Product toEntity(ProductCreateRequest request);
}
