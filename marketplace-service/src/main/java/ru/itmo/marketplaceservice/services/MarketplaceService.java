package ru.itmo.marketplaceservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.itmo.marketplaceservice.clients.ItemClient;
import ru.itmo.marketplaceservice.clients.UserClient;
import ru.itmo.marketplaceservice.exceptions.NotEnoughMoneyException;
import ru.itmo.marketplaceservice.exceptions.NotFoundException;
import ru.itmo.marketplaceservice.model.dto.ResponseDto;
import ru.itmo.marketplaceservice.model.dto.UpdateUserIdDto;
import ru.itmo.marketplaceservice.model.dto.UserUpdateBalanceDto;
import ru.itmo.marketplaceservice.model.entity.ItemEntity;
import ru.itmo.marketplaceservice.model.entity.MarketplaceItemEntity;
import ru.itmo.marketplaceservice.model.entity.UserEntity;
import ru.itmo.marketplaceservice.repositories.MarketplaceItemsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceItemsRepository marketplaceRepository;

    private final UserClient userClient;

    private final ItemClient itemClient;

    public Page<MarketplaceItemEntity> findAll(int minPrice, int maxPrice, Pageable pageable) {
        return marketplaceRepository.findAllByPriceBetween(minPrice, maxPrice, pageable);
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByName(String name) {
        return marketplaceRepository.findByItemNameStartingWith(name);
    }

    public List<MarketplaceItemEntity> findMarketplaceItemsByUser(String userName) throws NotFoundException {
        ResponseDto<UserEntity> response = userClient.findByUsername(userName);
        if (!response.code().is2xxSuccessful()) throw new NotFoundException("Пользователь с именем " + userName + " не найден");
        return marketplaceRepository.findByItemUser(response.body());
    }

    @Transactional
    public MarketplaceItemEntity createMarketplaceItem(
            String userName,
            Long itemId,
            int price
    ) throws NotFoundException, IllegalArgumentException {
        ResponseDto<ItemEntity> itemResponse = itemClient.findById(itemId);
        if (!itemResponse.code().is2xxSuccessful()) throw new NotFoundException("Айтем с id: " + itemId + " не найден");
        if (marketplaceRepository.existsByItem(itemResponse.body())) {
            throw new IllegalArgumentException("Айтем с id " + itemId + " уже находится на торговой площадке");
        }
        ResponseDto<UserEntity> userResponse = userClient.findByUsername(userName);
        if (!userResponse.code().is2xxSuccessful()) throw new NotFoundException("User with name " + userName + " not found");
        UserEntity seller = userResponse.body();
        if (!seller.getItems().contains(itemResponse.body())) {
            throw new IllegalArgumentException("Item with id " + itemId + " is not item of user");
        }
        MarketplaceItemEntity marketplaceItemEntity = new MarketplaceItemEntity();
        marketplaceItemEntity.setItem(itemResponse.body());
        marketplaceItemEntity.setPrice(price);
        return marketplaceRepository.save(marketplaceItemEntity);
    }
    @Transactional
    public void purchaseMarketplaceItem(String buyerUserName, Long marketplaceItemId) throws NotFoundException, NotEnoughMoneyException, IllegalArgumentException {
        ResponseDto<UserEntity> userResponse = userClient.findByUsername(buyerUserName);
        if (!userResponse.code().is2xxSuccessful()) throw new NotFoundException("User with name " + buyerUserName + " not found");
        UserEntity buyer = userResponse.body();
        MarketplaceItemEntity marketplaceItem = marketplaceRepository.findById(marketplaceItemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + marketplaceItemId + " not found on the marketplace"));
        if (marketplaceItem.getItem().getUser().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("Нельзя купить айтем у самого себя");
        }
        if (buyer.getBalance() < marketplaceItem.getPrice()) {
            throw new NotEnoughMoneyException();
        }
        UserEntity seller = marketplaceItem.getItem().getUser();
        userClient.updateBalance(new UserUpdateBalanceDto(seller.getId(), seller.getBalance() + marketplaceItem.getPrice()));
        userClient.updateBalance(new UserUpdateBalanceDto(buyer.getId(), buyer.getBalance() - marketplaceItem.getPrice()));
        ItemEntity item = marketplaceItem.getItem();
        itemClient.updateUserId(new UpdateUserIdDto(item.getId(), buyer.getId()));
        marketplaceRepository.deleteById(marketplaceItemId);
    }
    @Transactional
    public Optional<MarketplaceItemEntity> deleteMarketplaceItemById(Long itemId) {
        return marketplaceRepository.deleteMarketplaceItemById(itemId);
    }
    @Transactional
    public void deleteAllMarketplaceItems() {
        marketplaceRepository.deleteAll();
    }
}
