package ru.sfedu.teamselection.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Track;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {
    private Long id;

    private String name;

    private String about;

    @JsonProperty(value = "project_type")
    private String projectType;

    @JsonProperty(value = "quantity_of_students", defaultValue = "0")
    @Builder.Default
    private Integer quantityOfStudents = 0;

    @JsonProperty(value = "captain_id")
    @Builder.Default
    private Long captainId = 0L; //TODO check

    @JsonProperty(value = "is_full")
    @Builder.Default
    private Boolean isFull = false;

    @Builder.Default
    private String tags = ""; //TODO

    @JsonProperty(value = "current_track")
    @JsonBackReference
    private Track currentTrack;

    @JsonManagedReference
    private List<StudentDto> students;

    @Builder.Default
    private String candidates = ""; //TODO
}
