package ru.sfedu.teamselection.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Application;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreationDto {
    private String name;

    private String about;

    @JsonProperty(value = "project_type")
    private String projectType;

    @JsonProperty(value = "captain_id")
    @NotNull
    private Long captainId;

    @Builder.Default
    private String tags = ""; //TODO

    @JsonProperty(value = "current_track")
    @JsonBackReference
    private Long currentTrackId;
}
