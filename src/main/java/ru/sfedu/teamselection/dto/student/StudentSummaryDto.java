package ru.sfedu.teamselection.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentSummaryDto {
    private Integer course;

    @JsonProperty("group_number")
    private Integer groupNumber;

    @JsonProperty("current_team_id")
    private Long currentTeamId;

    @JsonProperty("current_team_name")
    private String currentTeamName;

    @JsonProperty("current_track_id")
    private Long currentTrackId;
}
