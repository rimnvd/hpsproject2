package ru.itmo.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.userservice.model.entity.RoleEntity;
import ru.itmo.userservice.model.enums.Role;
import ru.itmo.userservice.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public RoleEntity findByRole(Role role) {
        return repository.findByRole(role).orElseThrow();
    }

    public RoleEntity getStandardUserRole() {
        return findByRole(Role.STANDARD_USER);
    }

    public RoleEntity getAdminRole() {
        return findByRole(Role.ADMIN);
    }
    public RoleEntity getPremiumUserRole() {
        return findByRole(Role.PREMIUM_USER);
    }
    public RoleEntity getBlockedUserRole() {
        return findByRole(Role.BLOCKED_USER);
    }

}
