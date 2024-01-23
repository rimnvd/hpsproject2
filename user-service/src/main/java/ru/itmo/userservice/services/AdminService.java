package ru.itmo.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.userservice.clients.InventoryClient;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.exceptions.UserBlockedException;
import ru.itmo.userservice.model.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final RoleService roleService;
    private final InventoryClient inventoryClient;
    public void deleteUser(Long id) throws NotFoundException {
        if (!userService.existsById(id)) throw new NotFoundException("Пользователь с id " + id + " не найден");
        inventoryClient.deleteAll(id);
        userService.deleteById(id);
    }

    public void setAdminRole(Long id) throws NotFoundException, UserBlockedException {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        if (user.getRoles().contains(roleService.getBlockedUserRole())) throw new UserBlockedException("Пользователь не может быть админом, так как он в черном списке");
        if (!user.getRoles().contains(roleService.getAdminRole())) {
            user.addRole(roleService.getAdminRole());
            userService.save(user);
        }
    }

    public void removeAdminRole(Long id) throws NotFoundException {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        user.removeRole(roleService.getAdminRole());
        userService.save(user);
    }

    public void setPremiumUserRole(Long id) throws NotFoundException, UserBlockedException {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        if (user.getRoles().contains(roleService.getBlockedUserRole())) throw new UserBlockedException("Пользователь не может быть премиумом, так как он в черном списке");
        if (!user.getRoles().contains(roleService.getPremiumUserRole())) {
            user.removeRole(roleService.getStandardUserRole());
            user.addRole(roleService.getPremiumUserRole());
            userService.save(user);
        }
    }

    public void setBlockedUserRole(Long id) throws NotFoundException {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        user.clearRoles();
        user.addRole(roleService.getBlockedUserRole());
        userService.save(user);
    }

    public void setStandardUserRole(Long id) throws NotFoundException {
        UserEntity user = userService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        if (user.getRoles().contains(roleService.getBlockedUserRole())) {
            user.removeRole(roleService.getBlockedUserRole());
            user.addRole(roleService.getStandardUserRole());
        }
        if (user.getRoles().contains(roleService.getPremiumUserRole())) {
            user.removeRole(roleService.getPremiumUserRole());
            user.addRole(roleService.getStandardUserRole());
        }
        userService.save(user);
    }


}
