package ru.sfedu.teamselection.dto.track;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.sfedu.teamselection.dto.team.TeamDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackDto {
    private Long id;
    private String name;
    private String about;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private Integer minConstraint;
    private Integer maxConstraint;
    private Integer maxSecondCourseConstraint;
    @Builder.Default
    private List<TeamDto> currentTeams = new ArrayList<>();
}

