package ru.taf.exception;

public class MemoryPageNotFoundException extends RuntimeException{
    public MemoryPageNotFoundException(String message) {
        super(message);
    }
}
