package ru.itmo.userservice.clients;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.userservice.model.dto.ResponseDto;


@FeignClient(name = "inventory-client", url = "localhost:8081")
public interface InventoryClient {
    @DeleteMapping("inventory/delete-user-inventory/{userId}")
    ResponseDto<Object> deleteAll(@PathVariable @Positive Long userId);
}
