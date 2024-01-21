package ru.itmo.userservice.model.dto;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public record ResponseDto<T>(T body, Throwable e, HttpStatus code) implements Serializable {
}
