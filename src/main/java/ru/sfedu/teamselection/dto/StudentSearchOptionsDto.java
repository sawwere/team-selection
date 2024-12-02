package ru.sfedu.teamselection.dto;

import java.util.HashSet;
import java.util.List;
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
public class StudentSearchOptionsDto {
    private Set<Integer> courses = new HashSet<>();
    private Set<Integer> groups = new HashSet<>();

    private List<Boolean> hasTeam = List.of(true, false);
    private List<Boolean> isCaptain = List.of(true, false);

    private Set<TechnologyDto> technologies = new HashSet<>();
}
