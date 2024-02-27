package ru.itmo.marketplaceservice.clients;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.itmo.marketplaceservice.exceptions.ServiceUnavailableException;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UserDto;
import ru.itmo.marketplaceservice.model.dto.UserUpdateBalanceDto;

//@FeignClient(name = "user-client", url = "localhost:8081", fallback = UserClient.UserClientFallback.class)
@FeignClient(name = "user-client", url = "gateway-server:8081", fallback = UserClient.UserClientFallback.class)
public interface UserClient {

    @GetMapping(path="users/get-user-by-id")
    ResponseDto<UserDto> getById(@RequestParam @NotNull @Min(1) Long id);

    @GetMapping("users/get-user-by-username")
    ResponseDto<UserDto> findByUsername(@RequestParam @NotNull String username);

    @PostMapping("users/update-balance")
    ResponseEntity<String> updateBalance(@RequestBody @NotNull UserUpdateBalanceDto userUpdateBalanceDto);

    @Component
    class UserClientFallback implements UserClient {
        @Override
        public ResponseDto<UserDto> getById(@RequestParam @NotNull @Min(1) Long id) {
            throw new ServiceUnavailableException("User service not available");
        }

        @Override
        public ResponseDto<UserDto> findByUsername(@RequestParam @NotNull String username) {
            throw new ServiceUnavailableException("User service not available");
        }

        @Override
        public ResponseEntity<String> updateBalance(@RequestBody @NotNull UserUpdateBalanceDto userUpdateBalanceDto) {
            throw new ServiceUnavailableException("User service not available");
        }
    }
}
