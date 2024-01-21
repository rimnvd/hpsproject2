package ru.itmo.marketplaceservice.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyMarketplaceItemRequestDto {

    @Positive
    private Long itemId;
}
