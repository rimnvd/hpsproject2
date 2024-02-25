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
import ru.itmo.marketplaceservice.model.dto.UserDto;
import ru.itmo.marketplaceservice.model.dto.UserUpdateBalanceDto;

//@FeignClient(name = "user-client", url = "localhost:8081")
@FeignClient(name = "user-client", url = "gateway-server:8081")
public interface UserClient {
    @GetMapping(path="users/get-user-by-id")
    ResponseDto<UserDto> getById(@RequestParam @NotNull @Min(1) Long id);

    @GetMapping("users/get-user-by-username")
    ResponseDto<UserDto> findByUsername(@RequestParam @NotNull String username);

    @PostMapping("users/update-balance")
    ResponseEntity<String> updateBalance(@RequestBody @NotNull UserUpdateBalanceDto userUpdateBalanceDto);
}
