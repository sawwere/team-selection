package ru.sfedu.teamselection.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
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
public class ApplicationResponseDto {

    private Long id;
    @JsonProperty(value = "student_id")
    private long studentId;

    @JsonProperty(value = "team_id")
    private long teamId;

    private ApplicationStatus status;

    private ApplicationType type;

    @Builder.Default
    private List<ApplicationStatus> possibleTransitions = new ArrayList<>();
}
