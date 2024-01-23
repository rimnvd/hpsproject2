package ru.itmo.itemservice.controllers;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ItemDto;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.services.InventoryService;
import ru.itmo.itemservice.utils.DtoConverter;

import java.util.List;

@RequestMapping(path = "/inventory")
@RestController
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam @Positive Long userId) {
        try {
            List<ItemEntity> inventoryItemsEntities = inventoryService.findUserInventory(userId);
            List<ItemDto> inventoryItemsDtos = inventoryItemsEntities
                    .stream()
                    .map(DtoConverter::itemEntityToDto)
                    .toList();
            return ResponseEntity.ok(inventoryItemsDtos);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//    @GetMapping("/get-user-inventory")
//    public ResponseDto<List<ItemDto>> getAllUserItems(@RequestParam @Positive Long userId) {
//        try {
//            List<ItemEntity> inventoryItemsEntities = inventoryService.findUserInventory(userId);
//            List<ItemDto> items = inventoryItemsEntities
//                    .stream()
//                    .map(DtoConverter::itemEntityToDto)
//                    .toList();
//            return new ResponseDto<>(items, null, HttpStatus.OK);
//        } catch (NotFoundException e) {
//            return new ResponseDto<>(null, new NotFoundException(""), HttpStatus.NOT_FOUND);
//        }
//    }

    @DeleteMapping("/delete-user-inventory/{userId}")
    public ResponseDto<Object> deleteAll(@PathVariable @Positive Long userId) {
        try {
            inventoryService.deleteUserInventory(userId);
            return new ResponseDto<>(null, null, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseDto<>(null, null, HttpStatus.NOT_FOUND);
        }
    }
}
