package ru.sfedu.teamselection.service.validation;

public sealed class ValidationResult permits
        ValidationResult.Failure,
        ValidationResult.Forbidden,
        ValidationResult.Success {
    public static final class Success extends ValidationResult {

    }

    public static final class Failure extends ValidationResult {
        public final String message;

        public Failure(String message) {
            this.message = message;
        }
    }

    public static final class Forbidden extends ValidationResult {
        public final String message;

        public Forbidden(String message) {
            this.message = message;
        }
    }
}
