package ru.sfedu.teamselection.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.dto.TechnologyDto;


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
    @Builder.Default
    private List<TechnologyDto> technologies = new ArrayList<>();

    @NotNull
    @JsonProperty("captain_id")
    private Long captainId;

    /**
     * Здесь список только ID студентов.
     * Jackson легко десериализует <code>List<Long></code>
     */
    @NotNull
    @Builder.Default
    private Set<Long> studentIds = new HashSet<>();

    /** ID текущего трека **/
    @JsonProperty("current_track_id")
    private Long currentTrackId;
}
