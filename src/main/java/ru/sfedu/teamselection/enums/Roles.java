package ru.sfedu.teamselection.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    USER,
    ADMINISTRATOR,
    SUPER_ADMINISTRATOR;

    @Override
    public String getAuthority() {
        return toString();
    }
}
