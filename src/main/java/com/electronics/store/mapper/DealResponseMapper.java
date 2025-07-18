package com.electronics.store.mapper;

import com.electronics.store.dto.DealResponse;
import com.electronics.store.model.Deal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DealResponseMapper {
  @Mapping(source = "active", target = "active")
  DealResponse toDto(Deal deal);
}
