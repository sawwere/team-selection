package ru.sfedu.teamselection.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.enums.ApplicationStatus;

/**
 * DTO for {@link Application}
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCreationDto {

    private Long id;
    @JsonProperty(value = "student_id")
    private long studentId;

    @JsonProperty(value = "team_id")
    private long teamId;

    private ApplicationStatus status;

}
