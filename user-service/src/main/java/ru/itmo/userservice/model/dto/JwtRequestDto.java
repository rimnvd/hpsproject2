package ru.itmo.userservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@JsonDeserialize
@JsonSerialize
public class JwtRequestDto implements Serializable {
    private String username;
    private String password;
}
