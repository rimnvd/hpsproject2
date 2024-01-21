package ru.itmo.marketplaceservice.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellMarketplaceItemRequestDto {

    @Positive
    private Long itemId;

    @Positive
    @Max(1000000)
    private int price;
}
