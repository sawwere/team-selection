package ru.sfedu.teamselection.domain.application;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ApplicationType {
    INVITE("invite"),
    REQUEST("request");

    private final String type;

    ApplicationType(String type) {
        this.type = type;
    }

    @JsonCreator
    public static ApplicationType ignoreCaseOf(String status) {
        return ApplicationType.valueOf(status.toUpperCase());
    }

    @Override
    public String toString() {
        return type;
    }
}
