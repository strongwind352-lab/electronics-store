package com.electronics.store.mapper;

import com.electronics.store.dto.DealResponse;
import com.electronics.store.model.Deal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DealResponseMapper {
//    @Mapping()
    DealResponse toDto(Deal deal);
}
