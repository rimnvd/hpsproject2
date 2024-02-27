package ru.itmo.marketplaceservice.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ru.itmo.marketplaceservice.clients.ItemClient;
import ru.itmo.marketplaceservice.clients.UserClient;
import ru.itmo.marketplaceservice.exceptions.NotFoundException;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UserDto;
import ru.itmo.marketplaceservice.model.entity.MarketplaceItemEntity;
import ru.itmo.marketplaceservice.repositories.MarketplaceItemsRepository;
import ru.itmo.marketplaceservice.services.MarketplaceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MarketplaceServiceTests {

    @MockBean
    private MarketplaceItemsRepository marketplaceRepository;

    @MockBean
    private UserClient userClient;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MarketplaceService marketplaceService;

    @Test
    public void testFindMarketplaceItemsByName() {
        // given
        String itemName = "example";
        MarketplaceItemEntity item1 = new MarketplaceItemEntity(1L, 100, "nick", "AXE", 1L, "RARE");
        MarketplaceItemEntity item2 = new MarketplaceItemEntity(2L, 100, "bob", "ITEM", 2L, "STANDARD");
        when(marketplaceRepository.findByItemNameStartingWith(anyString())).thenReturn(List.of(item1, item2));

        // when - then
        StepVerifier.create(marketplaceService.findMarketplaceItemsByName(itemName))
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();
    }

    @Test
    public void testFindMarketplaceItemsByUser_UserFound() throws NotFoundException {
        // given
        String userName = "testuser";
        UserDto userDto = new UserDto(
                1L,
                userName,
                "test@example.com",
                100,
                "Description",
                Arrays.asList("ROLE_USER"),
                new ArrayList<>()
        );
        ResponseDto<UserDto> successResponse = new ResponseDto<>(userDto, null, HttpStatus.OK);
        when(userClient.findByUsername(anyString())).thenReturn(successResponse);

        MarketplaceItemEntity item1 = new MarketplaceItemEntity(1L, 100, "nick", "AXE", 1L, "RARE");
        MarketplaceItemEntity item2 = new MarketplaceItemEntity(2L, 100, "bob", "ITEM", 2L, "STANDARD");
        List<MarketplaceItemEntity> expectedItems = List.of(item1, item2);
        when(marketplaceRepository.findByUserName(anyString())).thenReturn(expectedItems);

        // when
        List<MarketplaceItemEntity> actualItems = marketplaceService.findMarketplaceItemsByUser(userName);

        // then
        assertEquals(expectedItems, actualItems);
    }
}
