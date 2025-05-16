package ru.sfedu.teamselection.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.sfedu.teamselection.dto.TechnologyDto;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @JsonProperty("project_description")
    @Size(max = 1024)
    private String projectDescription;

    @JsonProperty("project_type")
    @NotNull
    private ProjectTypeDto projectType;

    @JsonProperty("technologies")
    @NotNull
    private List<TechnologyDto> technologies;

    @NotNull
    private Long captainId;

    /**
     * Здесь список только ID студентов.
     * Jackson легко десериализует List<Long>.
     */
    @NotNull
    private List<Long> studentIds;

    /** ID текущего трека **/
    @JsonProperty("current_track_id")
    private Long currentTrackId;
}
