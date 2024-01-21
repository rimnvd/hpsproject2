package ru.itmo.marketplaceservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.marketplaceservice.model.enums.Rarity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItemDto {
    Long id;
    String name;
    Rarity rarity;
    int price;
}
