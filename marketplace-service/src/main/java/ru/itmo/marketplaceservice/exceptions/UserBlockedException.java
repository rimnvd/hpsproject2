package ru.itmo.marketplaceservice.exceptions;

public class UserBlockedException extends Exception {
    public UserBlockedException(String message) {
        super(message);
    }
}
