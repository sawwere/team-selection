package ru.sfedu.teamselection.dto.team;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.dto.student.StudentDto;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(scope = TeamDto.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TeamDto {
    private Long id;

    private String name;

    @JsonProperty(value = "project_description")
    @Size(max = 1024)
    private String projectDescription;

    @JsonProperty(value = "project_type")
    private ProjectTypeDto projectType;

    @JsonProperty(value = "quantity_of_students", defaultValue = "0")
    @Builder.Default
    private Integer quantityOfStudents = 0;

    @JsonProperty(value = "captain")
    @NotNull
    private StudentDto captain;

    @JsonProperty(value = "is_full")
    @Builder.Default
    private Boolean isFull = false;

    @JsonProperty(value = "current_track")
    private Long currentTrackId;

    @Builder.Default
    private List<StudentDto> students = new ArrayList<>();

    @JsonProperty(value = "showcase_ref")
    private String showcaseRef;

    @Builder.Default
    private List<ApplicationDto> applications = new ArrayList<>();

    @Builder.Default
    private List<TechnologyDto> technologies = new ArrayList<>();
}
