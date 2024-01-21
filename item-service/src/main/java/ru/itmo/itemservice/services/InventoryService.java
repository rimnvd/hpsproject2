package ru.itmo.itemservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.itmo.itemservice.clients.UserClient;
import ru.itmo.itemservice.exceptions.NotFoundException;
import ru.itmo.itemservice.model.dto.ResponseDto;
import ru.itmo.itemservice.model.entity.ItemEntity;
import ru.itmo.itemservice.model.entity.UserEntity;
import ru.itmo.itemservice.repositories.ItemsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemsRepository itemsRepository;

    private final UserClient userClient;

    public List<ItemEntity> findUserInventory(Long userId) throws NotFoundException {
        ResponseDto<UserEntity> response = userClient.getById(userId);
        if (!response.code().is2xxSuccessful()) throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        return itemsRepository.findByUser(response.body());
    }
}
