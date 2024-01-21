package ru.itmo.itemservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@JsonSerialize
@JsonDeserialize
public class UpdateUserIdDto implements Serializable {

    @NotNull
    @Min(1)
    Long userId;

    @NotNull
    @Min(1)
    Long itemId;
}
