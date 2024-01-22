package ru.itmo.userservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.userservice.model.entity.RoleEntity;
import ru.itmo.userservice.model.enums.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByRole(Role role);
}
