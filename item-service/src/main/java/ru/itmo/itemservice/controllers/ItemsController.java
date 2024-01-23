package ru.itmo.itemservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ItemDto;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.dto.UpdateUserIdDto;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.services.ItemsService;
import ru.itmo.itemservice.utils.DtoConverter;

import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemsController {

    private final ItemsService itemsService;

    @Autowired
    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> generateRandomItem(@Positive @RequestBody Long userId) {
        try {
            ItemEntity itemEntity = itemsService.generateRandomItemForUser(userId);
            ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity);
            return ResponseEntity.ok(itemDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/item")
    public ResponseEntity<?> getItemById(@Positive @RequestParam Long itemId) {
        Optional<ItemEntity> itemEntity = itemsService.findItemById(itemId);
        if (itemEntity.isPresent()) {
            ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity.get());
            return ResponseEntity.ok(itemDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Айтем с id: " + itemId + " не найден");
    }

    @DeleteMapping("/delete-item/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteItemById(@Positive @PathVariable Long itemId) {
        Optional<ItemEntity> itemEntity = itemsService.deleteItemById(itemId);
        if (itemEntity.isPresent()) {
            return ResponseEntity.ok("Айтем успешно удален");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Айтем с id: " + itemId + " не найден");
        }
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteAll() {
        itemsService.deleteAllItems();
        return ResponseEntity.ok("Все айтемы успешно удалены");
    }

    @GetMapping("/get-item")
    public ResponseDto<ItemEntity> getById(@Positive @RequestParam Long itemId) {
        ItemEntity item = itemsService.getById(itemId);
        if (item == null) {
            return new ResponseDto<>(null, new NotFoundException(""),HttpStatus.NOT_FOUND);
        }
        return new ResponseDto<>(item, null, HttpStatus.OK);
    }

    @PostMapping("change-user")
    public ResponseEntity<?> updateUserId(@RequestBody @NotNull @Valid UpdateUserIdDto updateUserIdDto) {
        try {
            itemsService.updateUserId(updateUserIdDto.getItemId(), updateUserIdDto.getUserId());
            return ResponseEntity.ok("");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
