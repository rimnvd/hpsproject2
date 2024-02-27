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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.dto.UserDto;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.model.enums.Rarity;
import ru.itmo.itemservice.repositories.ItemsRepository;
import ru.itmo.itemservice.services.ItemsService;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@Testcontainers
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemsServiceTests {

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
    private ItemsService itemsService;

    @Test
    public void testGenerateRandomItemForUser_UserFound() {
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

        ItemEntity randomItem = new ItemEntity(1L, "item1", Rarity.STANDARD, userId);
        ResponseDto<UserDto> userResponse = new ResponseDto<>(userDto, null, HttpStatus.OK);
        when(userClient.getById(userId)).thenReturn(userResponse);
        when(itemsRepository.save(any())).thenReturn(Mono.just(randomItem));

        // when - then
        StepVerifier.create(itemsService.generateRandomItemForUser(userId))
                .expectNext(randomItem)
                .verifyComplete();
    }

    @Test
    public void testGenerateRandomItemForUser_UserNotFound() {
        // given
        Long userId = 1L;
        ResponseDto<UserDto> userErrorResponse = new ResponseDto<>(null, new NotFoundException("Юзер с id: " + userId + " не найден"), HttpStatus.NOT_FOUND);

        when(userClient.getById(userId)).thenReturn(userErrorResponse);

        // when - then
        StepVerifier.create(itemsService.generateRandomItemForUser(userId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void testFindItemById_ItemFound() {
        // given
        Long itemId = 1L;
        ItemEntity foundItem = new ItemEntity(itemId, "item1", Rarity.STANDARD, 1L);
        when(itemsRepository.findById(itemId)).thenReturn(Mono.just(foundItem));

        // when - then
        StepVerifier.create(itemsService.findItemById(itemId))
                .expectNext(foundItem)
                .verifyComplete();
    }

    @Test
    public void testDeleteItemById_ItemDeleted() {
        // given
        Long itemId = 1L;
        ItemEntity itemToDelete = new ItemEntity(itemId, "item1", Rarity.STANDARD, 1L);
        when(itemsRepository.deleteItemById(itemId)).thenReturn(Mono.just(itemToDelete));

        // when - then
        StepVerifier.create(itemsService.deleteItemById(itemId))
                .expectNext(itemToDelete)
                .verifyComplete();
    }

    @Test
    public void testDeleteItemById_ItemNotFound() {
        // given
        Long itemId = 1L;
        when(itemsRepository.deleteItemById(anyLong())).thenReturn(Mono.empty());

        // when - then
        StepVerifier.create(itemsService.deleteItemById(itemId))
                .expectError(NotFoundException.class)
                .verify();
    }
}
