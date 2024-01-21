package ru.itmo.userservice.exceptions;

public class UserBlockedException extends Exception {
    public UserBlockedException(String message) {
        super(message);
    }
}
