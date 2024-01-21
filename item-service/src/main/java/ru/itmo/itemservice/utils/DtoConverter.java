package ru.itmo.itemservice.utils;

import ru.itmo.itemservice.model.dto.ItemDto;
import ru.itmo.itemservice.model.entity.ItemEntity;

public class DtoConverter {

    public static ItemDto itemEntityToDto(ItemEntity itemEntity) {
        return new ItemDto(itemEntity.getId(), itemEntity.getName(), itemEntity.getRarity());
    }

//    public static MarketplaceItemDto marketplaceItemEntityToDto(MarketplaceItemEntity marketplaceItemEntity) {
//        return new MarketplaceItemDto(
//                marketplaceItemEntity.getId(),
//                marketplaceItemEntity.getItem().getName(),
//                marketplaceItemEntity.getItem().getRarity(),
//                marketplaceItemEntity.getPrice()
//        );
//    }
}
