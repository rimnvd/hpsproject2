package ru.itmo.marketplaceservice.clients;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.itmo.marketplaceservice.exceptions.ServiceUnavailableException;
import ru.itmo.marketplaceservice.model.dto.ItemDto;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UpdateUserIdDto;

//@FeignClient(name = "item-client", url = "localhost:8081", fallback = ItemClient.ItemClientFallback.class)
@FeignClient(name = "item-client", url = "gateway-server:8081", fallback = ItemClient.ItemClientFallback.class)
public interface ItemClient {

    @GetMapping("items/get-item")
    ResponseDto<ItemDto> getById(@Positive @RequestParam Long itemId);

    @PostMapping("items/change-user")
    ResponseEntity<String> updateUserId(@RequestBody @NotNull @Valid UpdateUserIdDto updateUserIdDto);

    @Component
    class ItemClientFallback implements ItemClient {
        @Override
        public ResponseDto<ItemDto> getById(@Positive @RequestParam Long itemId) {
            throw new ServiceUnavailableException("Items service not available");
        }

        @Override
        public ResponseEntity<String> updateUserId(@RequestBody @NotNull @Valid UpdateUserIdDto updateUserIdDto) {
            throw new ServiceUnavailableException("Items service not available");
        }
    }
}
