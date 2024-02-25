package ru.itmo.itemservice.clients;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.itemservice.exceptions.ServiceUnavailableException;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.dto.UserDto;

//@FeignClient(name = "user-service", url = "localhost:8081", fallback = UserClient.UserClientFallback.class)
@FeignClient(name = "user-service", url = "gateway-server:8081", fallback = UserClient.UserClientFallback.class)
public interface UserClient {

    @GetMapping("users/get-user-by-id")
    ResponseDto<UserDto> getById(@RequestParam @NotNull @Min(1) Long id) throws ServiceUnavailableException;

    @Component
    class UserClientFallback implements UserClient {
        @Override
        public ResponseDto<UserDto> getById(Long id) {
            throw new ServiceUnavailableException("User service not available");
        }
    }
}
