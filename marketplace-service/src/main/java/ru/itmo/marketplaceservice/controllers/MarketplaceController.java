package ru.itmo.marketplaceservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.marketplaceservice.exceptions.NotEnoughMoneyException;
import ru.itmo.marketplaceservice.exceptions.NotFoundException;
import ru.itmo.marketplaceservice.model.dto.BuyMarketplaceItemRequestDto;
import ru.itmo.marketplaceservice.model.dto.SellMarketplaceItemRequestDto;
import ru.itmo.marketplaceservice.model.dto.MarketplaceItemDto;
import ru.itmo.marketplaceservice.model.entity.MarketplaceItemEntity;
import ru.itmo.marketplaceservice.services.MarketplaceService;
import ru.itmo.marketplaceservice.utils.ControllersConstants;
import ru.itmo.marketplaceservice.utils.DtoConverter;

import java.security.Principal;
import java.util.List;

@RequestMapping(path = "/marketplace")
@RestController
public class MarketplaceController {
    private final MarketplaceService marketplaceService;

    @Autowired
    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @Operation(
            description = "Get all items",
            summary = "Get all items from the marketplace",
            tags = "Marketplace"
    )
    @ApiResponse(responseCode = "200", description = "Success")
    @GetMapping
    public Mono<ResponseEntity<List<MarketplaceItemDto>>> getAll(
            @Positive @RequestParam(defaultValue = "1") int minPrice,
            @Positive @RequestParam(defaultValue = "1000000") int maxPrice,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortOrder
    ) {
        Sort sort = null;
        if (sortOrder != null) {
            if (sortOrder.equalsIgnoreCase("asc")) {
                sort = Sort.by(Sort.Order.asc("price"));
            } else if (sortOrder.equalsIgnoreCase("desc")) {
                sort = Sort.by(Sort.Order.desc("price"));
            }
        }

        PageRequest pageRequest = sort != null
                ? PageRequest.of(page, ControllersConstants.PAGE_SIZE, sort)
                : PageRequest.of(page, ControllersConstants.PAGE_SIZE);

        return marketplaceService.findAll(minPrice, maxPrice, pageRequest)
                .map(resultPage -> ResponseEntity.ok()
                        .header("X-Total-Count", String.valueOf(resultPage.getTotalElements()))
                        .body(resultPage.getContent().stream()
                                .map(DtoConverter::marketplaceItemEntityToDto)
                                .toList()));
    }

    @Operation(
            description = "Search",
            summary = "Search for the item name",
            tags = "Marketplace"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Item not found on the marketplace or user does not exist")
    })
    @GetMapping("/search")
    public Flux<MarketplaceItemDto> getMarketplaceItemsByName(@RequestParam String itemName) {
        return marketplaceService.findMarketplaceItemsByName(itemName)
                .map(DtoConverter::marketplaceItemEntityToDto);
    }

    @Operation(
            description = "Sell an item",
            summary = "Put up item for sale on the marketplace",
            tags = "Marketplace"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Item not found on the marketplace or user does not exist")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getMarketplaceItemsByUser(Principal principal) {
        try {
            List<MarketplaceItemEntity> marketplaceItemsEntities = marketplaceService.findMarketplaceItemsByUser(principal.getName());
            List<MarketplaceItemDto> marketplaceItemsDtos = marketplaceItemsEntities
                    .stream()
                    .map(DtoConverter::marketplaceItemEntityToDto)
                    .toList();
            return ResponseEntity.ok(marketplaceItemsDtos);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            description = "Sell an item",
            summary = "Put up item for sale on the marketplace",
            tags = "Marketplace"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights"),
            @ApiResponse(responseCode = "404", description = "Item not found on the marketplace or user does not exist"),
            @ApiResponse(responseCode = "400", description = "Item does not belong to user")
    })
    @PostMapping("/sell")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PREMIUM_USER')")
    public ResponseEntity<?> sellMarketplaceItem(
            Principal principal,
            @Valid @RequestBody SellMarketplaceItemRequestDto requestDto
    ) {
        try {
            MarketplaceItemEntity entity = marketplaceService.createMarketplaceItem(
                    principal.getName(),
                    requestDto.getItemId(),
                    requestDto.getPrice()
            );
            return ResponseEntity.ok(DtoConverter.marketplaceItemEntityToDto(entity));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            description = "Buy an item",
            summary = "Buy an item on the marketplace",
            tags = "Marketplace"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights"),
            @ApiResponse(responseCode = "404", description = "Item not found on the marketplace or user does not exist"),
            @ApiResponse(responseCode = "400", description = "User doesn't have enough money")
    })
    @PostMapping("/buy")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PREMIUM_USER', 'STANDARD_USER')")
    public ResponseEntity<?> buyMarketplaceItem(
            Principal principal,
            @Valid @RequestBody BuyMarketplaceItemRequestDto requestDto
    ) {
        try {
            marketplaceService.purchaseMarketplaceItem(principal.getName(), requestDto.getItemId());
            return ResponseEntity.ok("Покупка совершена успешно");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotEnoughMoneyException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            description = "Delete an item from the marketplace",
            summary = "Delete an item from the marketplace by id if exists",
            tags = "Marketplace"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @DeleteMapping("/delete/{itemId}")
    public Mono<ResponseEntity<String>> deleteItemById(@PathVariable @Positive Long itemId) {
        return marketplaceService.deleteMarketplaceItemById(itemId)
                .map(item -> ResponseEntity.ok("Айтем с id: " + itemId + " успешно удален"))
                .onErrorResume(NotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e)));
    }

    @Operation(
            description = "Delete all items",
            summary = "Delete all items from the marketplace",
            tags = "Marketplace"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<?>> deleteAll() {
        return marketplaceService.deleteAllMarketplaceItems()
                .then(Mono.just(ResponseEntity.ok("Все айтемы успешно удалены")));
    }
}
