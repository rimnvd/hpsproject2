package ru.itmo.itemservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    Collection<String> roles;
    List<ItemDto> items;

}
