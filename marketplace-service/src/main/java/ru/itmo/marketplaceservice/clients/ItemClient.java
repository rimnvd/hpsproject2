package ru.itmo.marketplaceservice.clients;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.marketplaceservice.model.dto.ItemDto;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UpdateUserIdDto;

//@FeignClient(name = "item-client", url = "localhost:8081")
@FeignClient(name = "item-client", url = "gateway-server:8081")
public interface ItemClient {

    @GetMapping("items/get-item")
    ResponseDto<ItemDto> getById(@Positive @RequestParam Long itemId);

    @PostMapping("items/change-user")
    ResponseEntity<String> updateUserId(@RequestBody @NotNull @Valid UpdateUserIdDto updateUserIdDto);
}
