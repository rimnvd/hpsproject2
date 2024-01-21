package ru.itmo.marketplaceservice.utils;

import ru.itmo.marketplaceservice.model.dto.MarketplaceItemDto;
import ru.itmo.marketplaceservice.model.entity.MarketplaceItemEntity;

public class DtoConverter {

    public static MarketplaceItemDto marketplaceItemEntityToDto(MarketplaceItemEntity marketplaceItemEntity) {
        return new MarketplaceItemDto(
                marketplaceItemEntity.getId(),
                marketplaceItemEntity.getItem().getName(),
                marketplaceItemEntity.getItem().getRarity(),
                marketplaceItemEntity.getPrice()
        );
    }
}
