package ru.itmo.itemservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.model.enums.Rarity;
import ru.itmo.itemservice.repositories.ItemsRepository;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItemsRepository itemsRepository;
    private final UserClient userClient;

    public Mono<ItemEntity> generateRandomItemForUser(Long userId) {
        return Mono.fromCallable(() -> {
            if (!userClient.getById(userId).code().is2xxSuccessful()) {
                throw new NotFoundException("Пользователь с id: " + userId + " не найден");
            }
            ItemEntity randomItem = createRandomItem(userId);
            return randomItem;
        }).flatMap(itemsRepository::save);
    }

    public Mono<ItemEntity> findItemById(Long itemId) {
        return itemsRepository.findById(itemId)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Айтем с id: " + itemId + " не найден")));
    }

    public Mono<ItemEntity> deleteItemById(Long itemId) {
        return itemsRepository.deleteItemById(itemId)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Айтем с id: " + itemId + " не найден")));
    }

    public Mono<Void> deleteAllItems() {
        return itemsRepository.deleteAll();
    }

    public Mono<?> updateUserId(Long itemId, Long newUserId) {
        return findItemById(itemId)
                .switchIfEmpty(Mono.error(new NotFoundException("Айтем с id: " + itemId + " не найден")))
                .flatMap(item -> Mono.defer(() -> {
                    if (!userClient.getById(newUserId).code().is2xxSuccessful()) {
                        return Mono.error(new NotFoundException("Пользователь с id: " + newUserId + " не найден"));
                    }
                    item.setUserId(newUserId);
                    return itemsRepository.save(item);
                }));
    }

    // MARK: - Private
    private ItemEntity createRandomItem(Long userId) {
        String[] titles = {"Sword", "Axe", "Pick", "Archery"};
        Rarity[] rarities = Rarity.values();

        Random random = new Random();

        String randomTitle = titles[random.nextInt(titles.length)];
        Rarity randomRarity = rarities[random.nextInt(rarities.length)];

        return new ItemEntity(null, randomTitle, randomRarity, userId);
    }
}
