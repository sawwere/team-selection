package ru.sfedu.teamselection.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ApplicationStatus {
    ACCEPTED("accepted"),
    SENT("sent"),
    REJECTED("rejected"),
    CANCELLED("cancelled");

    private final String status;

    ApplicationStatus(String status) {
        this.status = status;
    }

    @JsonCreator
    public static ApplicationStatus of(String status) {
        return ApplicationStatus.valueOf(status.toUpperCase());
    }

    @Override
    public String toString() {
        return status;
    }
}
