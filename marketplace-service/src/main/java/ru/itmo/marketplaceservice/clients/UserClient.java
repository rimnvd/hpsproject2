package ru.itmo.marketplaceservice.clients;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UserUpdateBalanceDto;
import ru.itmo.marketplaceservice.model.entity.UserEntity;

@FeignClient(name = "user-client", url = "localhost:8081")
public interface UserClient {
    @GetMapping(path="users/get-user")
    ResponseDto<UserEntity> getById(@RequestParam @NotNull @Min(1) Long id);

    @GetMapping("users/find")
    ResponseDto<UserEntity> findByUsername(@RequestParam @NotNull String username);

    @PostMapping("users/update-balance")
    ResponseEntity<?> updateBalance(@RequestBody @NotNull UserUpdateBalanceDto userUpdateBalanceDto);

}
