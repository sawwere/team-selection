package ru.sfedu.teamselection.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link ru.sfedu.teamselection.domain.ProjectType}
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTypeDto {
    private Long id;
    @Size(min = 3, max = 32)
    @NotBlank
    private String name;
}
