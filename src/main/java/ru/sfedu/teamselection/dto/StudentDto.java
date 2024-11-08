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
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;


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
    private Boolean hasTeam = null;
    private Boolean isCaptain = null;

    @JsonProperty(value = "current_team")
    private Team currentTeam;

    private List<TechnologyDto> technologies = new ArrayList<>();

    private List<ApplicationDto> applications = new ArrayList<>();

    private User user;
}
