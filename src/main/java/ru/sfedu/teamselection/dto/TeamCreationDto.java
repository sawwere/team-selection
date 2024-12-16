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


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreationDto {

    private Long id;

    private String name;

    @JsonProperty(value = "projectDescription")
    @Size(max = 1024)
    private String projectDescription;

    @JsonProperty(value = "projectType")
    private String projectType;

    @JsonProperty(value = "captainId")
    @NotNull
    private Long captainId;

    @Builder.Default
    private List<TechnologyDto> technologies = new ArrayList<>();

    @JsonProperty(value = "currentTrackId")
    private Long currentTrackId;
}
