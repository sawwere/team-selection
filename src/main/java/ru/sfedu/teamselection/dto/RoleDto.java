package ru.sfedu.teamselection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

/**
 * DTO for {@link ru.sfedu.teamselection.domain.Role}
 */
@Value
@Builder
@Getter
@Setter
@AllArgsConstructor
public class RoleDto {
    Long id;
    String name;
}
