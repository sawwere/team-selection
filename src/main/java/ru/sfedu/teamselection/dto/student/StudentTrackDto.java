package ru.sfedu.teamselection.dto.student;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTrackDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String about;
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private String type;
    @Min(0)
    private Integer minConstraint;
    @Min(0)
    private Integer maxConstraint;
    @Min(0)
    private Integer maxSecondCourseConstraint;
}
