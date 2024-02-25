package ru.itmo.userservice.clients;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.userservice.exceptions.ServiceUnavailableException;
import ru.itmo.userservice.model.dto.ResponseDto;


//@FeignClient(name = "inventory-client", url = "localhost:8081")
@FeignClient(name = "inventory-client",url = "gateway-server:8081", fallback = InventoryClient.InventoryClientFallback.class)
public interface InventoryClient {
    @DeleteMapping("inventory/delete/{userId}")
    ResponseEntity<String> deleteAll(@PathVariable @Positive Long userId);

    @Component
    class InventoryClientFallback implements InventoryClient {
        @Override
        public ResponseEntity<String> deleteAll(@PathVariable @Positive Long userId) {
            throw new ServiceUnavailableException("Items service not available");
        }
    }
}
