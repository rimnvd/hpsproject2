package ru.itmo.itemservice.controllers;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ItemDto;
import ru.itmo.itemservice.model.dto.ResponseDto;
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
    public Mono<ResponseEntity<List<ItemDto>>> getAll(@RequestParam Long userId) {
        return inventoryService.findUserInventory(userId)
                .collectList()
                .flatMap(inventoryItemsEntities -> {
                    List<ItemDto> inventoryItemsDtos = inventoryItemsEntities
                            .stream()
                            .map(DtoConverter::itemEntityToDto)
                            .toList();

                    return Mono.just(ResponseEntity.ok(inventoryItemsDtos));
                })
                .onErrorResume(NotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage())));
    }

    @DeleteMapping("/delete/{userId}")
    public Mono<ResponseDto<Object>> deleteAll(@PathVariable @Positive Long userId) {
        return inventoryService.deleteUserInventory(userId)
                .thenReturn(new ResponseDto<>(null, null, HttpStatus.OK))
                .onErrorReturn(new ResponseDto<>(null, null, HttpStatus.NOT_FOUND));
    }
}
