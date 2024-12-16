package ru.sfedu.teamselection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Student;


/**
 * DTO for {@link Student}
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private Long id;

    private Integer course;

    @JsonProperty(value = "group_number")
    private Integer groupNumber;

    @JsonProperty(value = "about_self")
    @Size(max = 1024)
    private String aboutSelf;

    @Size(max = 255)
    private String contacts;

    @NotNull
    @JsonProperty(value = "has_team")
    private Boolean hasTeam = false;

    @NotNull
    @JsonProperty(value = "is_captain")
    private Boolean isCaptain = false;

    @JsonProperty(value = "current_team")
    private TeamDto currentTeam;

    private List<TechnologyDto> technologies = new ArrayList<>();

    private List<ApplicationDto> applications = new ArrayList<>();

    private UserDto user;
}
