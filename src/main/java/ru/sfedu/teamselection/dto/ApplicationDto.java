package ru.sfedu.teamselection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private Long id;

    @JsonProperty(value = "student")
    private StudentCreationDto student;

    @JsonProperty(value = "team")
    private TeamCreationDto team;

    private String status;

}
