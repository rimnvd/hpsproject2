package ru.itmo.itemservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
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
    public Mono<ResponseEntity<ItemDto>> generateRandomItem(@Positive @RequestParam Long userId) {
        return itemsService.generateRandomItemForUser(userId)
                .map(itemEntity -> {
                    ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity);
                    return ResponseEntity.ok(itemDto);
                })
                .onErrorResume(NotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e)));
    }

    @GetMapping("/item")
    public Mono<ResponseEntity<ItemDto>> getItemById(@Positive @RequestParam Long itemId) {
        return itemsService.findItemById(itemId)
                .map(itemEntity -> {
                    ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity);
                    return ResponseEntity.ok(itemDto);
                })
                .onErrorResume(NotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e)));
    }

    @DeleteMapping("/delete/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<String>> deleteItemById(@Positive @PathVariable Long itemId) {
        return itemsService.deleteItemById(itemId)
                .map(itemEntity -> ResponseEntity.ok("Айтем с id: " + itemId + " успешно удален"))
                .onErrorResume(NotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e)));
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<String>> deleteAll() {
        return itemsService.deleteAllItems()
                .then(Mono.just(ResponseEntity.ok("Все айтемы успешно удалены")))
                .onErrorResume(Exception.class, e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка удаления айтемов: " + e.getMessage())));
    }

    @GetMapping("/get-item")
    public Mono<ResponseDto<ItemEntity>> getById(@Positive @RequestParam Long itemId) {
        return itemsService.findItemById(itemId)
                .map(itemEntity -> {
                    return new ResponseDto<>(itemEntity, null, HttpStatus.OK);
                })
                .onErrorResume(NotFoundException.class, e -> Mono.just(new ResponseDto<>(null, e, HttpStatus.NOT_FOUND)));
    }

    @PostMapping("change-user")
    public Mono<ResponseEntity<String>> updateUserId(@RequestBody @NotNull @Valid UpdateUserIdDto updateUserIdDto) {
        return itemsService.updateUserId(updateUserIdDto.getItemId(), updateUserIdDto.getUserId())
                .then(Mono.just(ResponseEntity.ok("user_id успешно изменен")))
                .onErrorResume(NotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }
}
