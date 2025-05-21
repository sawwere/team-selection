package ru.sfedu.teamselection.dto.student;

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
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;


/**
 * DTO for {@link Student}
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(scope = StudentDto.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    private Boolean hasTeam;

    @NotNull
    @JsonProperty(value = "is_captain")
    private Boolean isCaptain;

    @JsonProperty(value="teams")
    private List<TeamDto>  teams;

    @JsonProperty(value = "current_team")
    private TeamDto currentTeam;

    @Builder.Default
    private List<TechnologyDto> technologies = new ArrayList<>();

    @Builder.Default
    private List<ApplicationDto> applications = new ArrayList<>();

    @JsonProperty(value = "current_track")
    @NotNull
    private TrackCreationDto track;

    private UserDto user;
}
