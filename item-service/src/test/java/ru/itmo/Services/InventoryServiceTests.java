package ru.itmo.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.dto.UserDto;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.model.enums.Rarity;
import ru.itmo.itemservice.repositories.ItemsRepository;
import ru.itmo.itemservice.services.InventoryService;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InventoryServiceTests {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:9.6.8")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @MockBean
    private ItemsRepository itemsRepository;

    @MockBean
    private UserClient userClient;

    @Autowired
    private InventoryService inventoryService;

    @Test
    void findUserInventory_WithValidUserId_ReturnsUserInventory() {

        // given
        Long userId = 1L;
        UserDto userDto = new UserDto(
                userId, "testuser",
                "test@example.com",
                100,
                "Description",
                Arrays.asList("ROLE_USER"),
                new ArrayList<>()
        );

        ItemEntity item1 = new ItemEntity(1L, "item1", Rarity.STANDARD, userId);
        ItemEntity item2 = new ItemEntity(2L, "item2", Rarity.RARE, userId);

        ResponseDto<UserDto> successResponse = new ResponseDto<>(userDto, null, HttpStatus.OK);

        when(userClient.getById(userId)).thenReturn(successResponse);

        when(itemsRepository.findItemEntitiesByUserId(anyLong())).thenReturn(Flux.just(item1, item2));

        // when
        Flux<ItemEntity> inventory = inventoryService.findUserInventory(userId);

        // then
        StepVerifier.create(inventory)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void testFindUserInventory_UserNotFound() {
        // given
        Long userId = 1L;

        ResponseDto<UserDto> userErrorResponse = new ResponseDto<>(null, new NotFoundException("Юзер с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);

        when(userClient.getById(userId)).thenReturn(userErrorResponse);

        // when
        Flux<ItemEntity> inventory = inventoryService.findUserInventory(userId);

        // then
        StepVerifier.create(inventory)
                .expectError(NotFoundException.class)
                .verify();
    }
}