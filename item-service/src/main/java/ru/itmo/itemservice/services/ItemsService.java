package ru.itmo.itemservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.model.entity.UserEntity;
import ru.itmo.itemservice.model.enums.Rarity;
import ru.itmo.itemservice.repositories.ItemsRepository;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItemsRepository itemsRepository;
    private final UserClient userClient;

    @Transactional
    public ItemEntity generateRandomItemForUser(Long userId) throws NotFoundException {
        ResponseDto<UserEntity> response = userClient.getById(userId);
        if (!response.code().is2xxSuccessful()) throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        ItemEntity randomItem = createRandomItem();
        randomItem.setUser(response.body());
        return itemsRepository.save(randomItem);
    }

    public Optional<ItemEntity> findItemById(Long itemId) {
        return itemsRepository.findById(itemId);
    }

    @Transactional
    public Optional<ItemEntity> deleteItemById(Long itemId) {
        return itemsRepository.deleteItemById(itemId);
    }

    @Transactional
    public void deleteAllItems() {
        itemsRepository.deleteAll();
    }

    @Transactional
    public void updateUserId(Long itemId, Long newUserId) throws NotFoundException {
        ItemEntity item = findItemById(itemId).orElseThrow(() -> new NotFoundException("Айтем с id: " + itemId + " не найден"));
        ResponseDto<UserEntity> response = userClient.getById(newUserId);
        if (!response.code().is2xxSuccessful()) throw new NotFoundException("Пользователь с id: " + newUserId + " не найден");
        item.setUser(response.body());
        itemsRepository.save(item);
    }

    // MARK: - Private
    private ItemEntity createRandomItem() {
        String[] titles = {"Sword", "Axe", "Pick", "Archery"};
        Rarity[] rarities = Rarity.values();

        Random random = new Random();

        String randomTitle = titles[random.nextInt(titles.length)];
        Rarity randomRarity = rarities[random.nextInt(rarities.length)];

        return new ItemEntity(null, randomTitle, randomRarity, null);
    }
}
