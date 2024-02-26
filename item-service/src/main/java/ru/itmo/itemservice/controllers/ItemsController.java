package ru.itmo.itemservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
import ru.itmo.itemservice.services.ItemsService;
import ru.itmo.itemservice.utils.DtoConverter;

@RestController
@RequestMapping("/items")
@RefreshScope
public class ItemsController {

    @Value("${example.property}")
    private String refreshingExampleProperty;
    private final ItemsService itemsService;

    @Autowired
    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @Operation(
            description = "generate item for user",
            summary = "generate random item for user if user exists",
            tags = "item"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item was generated successfully"),
            @ApiResponse(responseCode = "404", description = "user not found")
    })
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
    public ResponseDto<ItemDto> getById(@Positive @RequestParam Long itemId) {
        return itemsService.findItemById(itemId)
                .map(itemEntity -> {
                    ItemDto itemDto = DtoConverter.itemEntityToDto(itemEntity);
                    return new ResponseDto<>(itemDto, null, HttpStatus.OK);
                })
                .onErrorResume(NotFoundException.class, e -> Mono.just(new ResponseDto<>(null, e, HttpStatus.NOT_FOUND))).block();
    }

    @PostMapping("change-user")
    public ResponseEntity<String> updateUserId(@RequestBody @NotNull @Valid UpdateUserIdDto updateUserIdDto) {
        return itemsService.updateUserId(updateUserIdDto.getItemId(), updateUserIdDto.getUserId())
                .then(Mono.just(ResponseEntity.ok("user_id успешно изменен")))
                .onErrorResume(NotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()))).block();
    }

    @GetMapping("/example-property")
    public String getExampleProperty() {
        return "Value of example.property: " + refreshingExampleProperty;
    }
}
