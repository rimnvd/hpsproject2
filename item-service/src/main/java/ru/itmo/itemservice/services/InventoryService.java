package ru.itmo.itemservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.repositories.ItemsRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemsRepository itemsRepository;

    private final UserClient userClient;

    public Flux<ItemEntity> findUserInventory(Long userId) {
        return Mono.fromCallable(() -> userClient.getById(userId))
                .flatMapMany(response -> {
                    if (response.code().is2xxSuccessful()) {
                        return itemsRepository.findItemEntitiesByUserId(userId);
                    } else {
                        return Mono.error(new NotFoundException("Юзер с id: " + userId + " не найден"));
                    }
                });
    }

    public Mono<Void> deleteUserInventory(Long userId) {
        return Mono.fromCallable(() -> userClient.getById(userId))
                .flatMap(response -> {
                    if (response.code().is2xxSuccessful()) {
                        return itemsRepository.deleteItemEntitiesByUserId(userId);
                    } else {
                        return Mono.error(new NotFoundException("Пользователь с id: " + userId + " не найден"));
                    }
                });
    }
}
