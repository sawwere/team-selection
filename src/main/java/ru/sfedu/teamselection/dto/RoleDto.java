package ru.sfedu.teamselection.dto;

import lombok.Value;

/**
 * DTO for {@link ru.sfedu.teamselection.domain.Role}
 */
@Value
public class RoleDto {
    Long id;
    String name;
}