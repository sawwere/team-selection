package ru.sfedu.teamselection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Technology;

/**
 * DTO for {@link Technology}
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyDto {
    private Long id;
    private String name;
}
