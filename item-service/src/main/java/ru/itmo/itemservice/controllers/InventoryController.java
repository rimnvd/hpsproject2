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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


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
    @Operation(
            description = "get user inventory",
            summary = "get user inventory by id if exists",
            tags = "inventory"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user inventory found"),
            @ApiResponse(responseCode = "404", description = "user not found")
    })
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
    @Operation(
            description = "delete user inventory",
            summary = "delete user inventory by id if exists",
            tags = "inventory"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user inventory deleted"),
            @ApiResponse(responseCode = "404", description = "user not found")
    })
    public ResponseEntity<String> deleteAll(@PathVariable @Positive Long userId) {
        return inventoryService.deleteUserInventory(userId)
                .thenReturn(ResponseEntity.ok("Успешно удалено"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с id " + userId + " не найден")).block();
    }
}
