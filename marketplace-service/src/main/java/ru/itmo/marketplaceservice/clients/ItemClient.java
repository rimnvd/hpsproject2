package ru.itmo.marketplaceservice.clients;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UpdateUserIdDto;
import ru.itmo.marketplaceservice.model.entity.ItemEntity;

@FeignClient(name = "user-service", url = "localhost:8081")
public interface ItemClient {
    @GetMapping("items/find")
    ResponseDto<ItemEntity> findById(@Positive @RequestParam Long itemId);

    @PostMapping("items/change-user")
    public ResponseEntity<?> updateUserId(@RequestBody @NotNull UpdateUserIdDto updateUserIdDto);
}
