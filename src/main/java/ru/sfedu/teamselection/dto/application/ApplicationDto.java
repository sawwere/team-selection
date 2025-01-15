package ru.sfedu.teamselection.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private Long id;

    @JsonProperty(value = "student")
    private StudentApplicationDto student;

    @JsonProperty(value = "team")
    private TeamCreationDto team;

    private String status;

}
