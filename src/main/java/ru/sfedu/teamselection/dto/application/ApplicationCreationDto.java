package ru.sfedu.teamselection.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.ApplicationType;
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

    @NotNull
    @JsonProperty(value = "student_id")
    private long studentId;

    @NotNull
    @JsonProperty(value = "team_id")
    private long teamId;

    @NotNull
    private ApplicationStatus status;

    @NotNull
    private ApplicationType type;
}
