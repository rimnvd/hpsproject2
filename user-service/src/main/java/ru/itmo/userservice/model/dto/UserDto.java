package ru.itmo.userservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import ru.itmo.hpsproject.model.entity.ItemEntity;
import ru.itmo.userservice.model.entity.RoleEntity;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    Long id;
    String username;
    String email;
    Integer balance;
    String description;
//    List<ItemEntity> items;
    Collection<RoleEntity> roles;

}
