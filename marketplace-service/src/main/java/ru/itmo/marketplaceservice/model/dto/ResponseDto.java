package ru.itmo.marketplaceservice.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@JsonSerialize
@JsonDeserialize
public record ResponseDto<T>(T body, Throwable e, HttpStatus code) implements Serializable {
}
