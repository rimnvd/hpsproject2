package ru.itmo.marketplaceservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import ru.itmo.marketplaceservice.clients.ItemClient;
import ru.itmo.marketplaceservice.clients.UserClient;
import ru.itmo.marketplaceservice.exceptions.NotEnoughMoneyException;
import ru.itmo.marketplaceservice.exceptions.NotFoundException;
import ru.itmo.marketplaceservice.model.dto.*;
import ru.itmo.marketplaceservice.model.entity.MarketplaceItemEntity;
import ru.itmo.marketplaceservice.repositories.MarketplaceItemsRepository;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceItemsRepository marketplaceRepository;

    private final UserClient userClient;

    private final ItemClient itemClient;

    public Mono<Page<MarketplaceItemEntity>> findAll(int minPrice, int maxPrice, Pageable pageable) {
        return Mono.fromCallable(() -> marketplaceRepository.findAllByPriceBetween(minPrice, maxPrice, pageable))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<MarketplaceItemEntity> findMarketplaceItemsByName(String name) {
        return Flux.defer(() -> Flux.fromIterable(marketplaceRepository.findByItemNameStartingWith(name)));
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByUser(String userName) throws NotFoundException {
        ResponseDto<UserDto> response = userClient.findByUsername(userName);
        if (!response.code().is2xxSuccessful()) throw new NotFoundException("Пользователь с именем " + userName + " не найден");
        return marketplaceRepository.findByUserName(response.body().getUsername());
    }

    @Transactional
    public MarketplaceItemEntity createMarketplaceItem(
            String userName,
            Long itemId,
            int price
    ) throws NotFoundException, IllegalArgumentException {
        ResponseDto<ItemDto> itemResponse = itemClient.getById(itemId);

        if (!itemResponse.code().is2xxSuccessful()) throw new NotFoundException("Айтем с id: " + itemId + " не найден");
        if (marketplaceRepository.existsByItemId(itemResponse.body().getId())) {
            throw new IllegalArgumentException("Айтем с id " + itemId + " уже находится на торговой площадке");
        }
        ResponseDto<UserDto> userResponse = userClient.findByUsername(userName);
        if (!userResponse.code().is2xxSuccessful()) throw new NotFoundException("User with name " + userName + " not found");

        if (!(userResponse.body().getId().equals(itemResponse.body().getUserId()))) throw new IllegalArgumentException("Айтем не принадлежит пользователю");

        MarketplaceItemEntity marketplaceItemEntity = new MarketplaceItemEntity();
        marketplaceItemEntity.setItemId(itemResponse.body().getId());
        marketplaceItemEntity.setItemName(itemResponse.body().getName());
        marketplaceItemEntity.setRarity(itemResponse.body().getRarity());
        marketplaceItemEntity.setPrice(price);
        marketplaceItemEntity.setUserName(userResponse.body().getUsername());
        return marketplaceRepository.save(marketplaceItemEntity);
    }

    @Transactional
    public void purchaseMarketplaceItem(String buyerUserName, Long marketplaceItemId) throws NotFoundException, NotEnoughMoneyException, IllegalArgumentException {
        ResponseDto<UserDto> userResponse = userClient.findByUsername(buyerUserName);
        if (!userResponse.code().is2xxSuccessful()) throw new NotFoundException("User with name " + buyerUserName + " not found");
        UserDto buyer = userResponse.body();
        MarketplaceItemEntity marketplaceItem = marketplaceRepository.findById(marketplaceItemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + marketplaceItemId + " not found on the marketplace"));
        if (marketplaceItem.getUserName().equals(buyer.getUsername())) {
            throw new IllegalArgumentException("Нельзя купить айтем у самого себя");
        }
        if (buyer.getBalance() < marketplaceItem.getPrice()) {
            throw new NotEnoughMoneyException();
        }
        ResponseDto<UserDto> seller = userClient.findByUsername(marketplaceItem.getUserName());
        ResponseEntity<String> updateBalanceResult1 = userClient.updateBalance(new UserUpdateBalanceDto(seller.body().getUsername(), seller.body().getBalance() + marketplaceItem.getPrice()));
        if (!updateBalanceResult1.getStatusCode().is2xxSuccessful()) throw new IllegalArgumentException("Нельзя совершить данную покупку");
        ResponseEntity<String> updateBalanceResult2 = userClient.updateBalance(new UserUpdateBalanceDto(buyer.getUsername(), buyer.getBalance() - marketplaceItem.getPrice()));
        if (!updateBalanceResult2.getStatusCode().is2xxSuccessful()) throw new IllegalArgumentException("Нельзя совершить данную покупку");
        Long itemId = marketplaceItem.getItemId();
        ResponseEntity<String> updateIdResult = itemClient.updateUserId(new UpdateUserIdDto(buyer.getId(), itemId));
        if (!updateIdResult.getStatusCode().is2xxSuccessful()) throw new IllegalArgumentException("Нельзя совершить данную покупку");
        marketplaceRepository.deleteById(marketplaceItemId);
    }

    public Mono<MarketplaceItemEntity> deleteMarketplaceItemById(Long itemId) {
        return Mono.defer(() -> {
            Optional<MarketplaceItemEntity> optionalItem = marketplaceRepository.deleteMarketplaceItemById(itemId);
            if (optionalItem.isPresent()) {
                return Mono.just(optionalItem.get());
            } else {
                return Mono.error(new NotFoundException("Айтем с id: " + itemId + " не найден"));
            }
        });
    }

    public Mono<Void> deleteAllMarketplaceItems() {
        return Mono.fromRunnable(() -> marketplaceRepository.deleteAll());
    }
}
