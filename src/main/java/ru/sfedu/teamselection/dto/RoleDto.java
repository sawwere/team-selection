package ru.sfedu.teamselection.dto;

import lombok.*;

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
