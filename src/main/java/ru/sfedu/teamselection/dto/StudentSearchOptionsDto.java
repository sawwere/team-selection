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
    @Builder.Default
    private Set<Integer> courses = new HashSet<>();
    @Builder.Default
    private Set<Integer> groups = new HashSet<>();
    @Builder.Default
    private List<Boolean> hasTeam = List.of(true, false);
    @Builder.Default
    private List<Boolean> isCaptain = List.of(true, false);
    @Builder.Default
    private Set<TechnologyDto> technologies = new HashSet<>();
}
