package ru.itmo.marketplaceservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonDeserialize
public class UserUpdateBalanceDto implements Serializable {
    @NotNull
    @Min(1)
    Long userId;

    @NotNull
    @Min(1)
    Integer newBalance;
}
