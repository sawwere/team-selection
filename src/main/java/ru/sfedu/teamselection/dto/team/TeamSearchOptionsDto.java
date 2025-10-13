package ru.sfedu.teamselection.dto.team;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.dto.ProjectTypeDto;
import ru.sfedu.teamselection.dto.TechnologyDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamSearchOptionsDto {
    @Builder.Default
    private Set<TechnologyDto> technologies = new HashSet<>();
    @Builder.Default
    private Set<ProjectTypeDto> projectTypes = new HashSet<>();
}
