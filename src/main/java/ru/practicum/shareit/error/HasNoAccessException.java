package ru.practicum.shareit.error;

public class HasNoAccessException extends RuntimeException {
    public HasNoAccessException(String message) {
        super(message);
    }
}

