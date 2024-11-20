package ru.sfedu.teamselection.exception;

/**
 * This exception is for situations where an action cannot be performed due to some limitations of the business logic
 */
public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String message) {
    }
}
