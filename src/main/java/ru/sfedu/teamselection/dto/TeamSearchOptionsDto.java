package ru.sfedu.teamselection.dto;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamSearchOptionsDto {
    private Set<TechnologyDto> technologies = new HashSet<>();

    private Set<String> projectTypes = new HashSet<>();
}
