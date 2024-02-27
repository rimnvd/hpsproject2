package ru.itmo.itemservice.utils;

import ru.itmo.itemservice.model.dto.ItemDto;
import ru.itmo.itemservice.model.entity.ItemEntity;

public class DtoConverter {

    public static ItemDto itemEntityToDto(ItemEntity itemEntity) {
        return new ItemDto(
                itemEntity.getId(),
                itemEntity.getName(),
                itemEntity.getRarity().name(),
                itemEntity.getUserId()
        );
    }
}
