package ru.sfedu.teamselection.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackCreationDto {
    private Long id;
    private String name;
    private String about;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private Integer minConstraint;
    private Integer maxConstraint;
    private Integer maxSecondCourseConstraint;
}
