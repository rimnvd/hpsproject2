package ru.itmo.itemservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.repositories.ItemsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemsRepository itemsRepository;

    private final UserClient userClient;

    public List<ItemEntity> findUserInventory(Long userId) throws NotFoundException {
        if (!userClient.getById(userId).code().is2xxSuccessful()) throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        return itemsRepository.findItemEntitiesByUserId(userId);
    }

    @Transactional
    public void deleteUserInventory(Long userId) throws NotFoundException {
        if (!userClient.getById(userId).code().is2xxSuccessful()) throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        itemsRepository.deleteItemEntitiesByUserId(userId);
    }
}
