package ru.itmo.itemservice.clients;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.entity.UserEntity;

@FeignClient(name = "user-service", url = "localhost:8081")
public interface UserClient {
    @GetMapping(path="users/get-user")
    ResponseDto<UserEntity> getById(@RequestParam @NotNull @Min(1) Long id);

}
