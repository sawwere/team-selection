package ru.sfedu.teamselection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private Long id;

    private String fio;

    private String email;

    private Integer course;

    @JsonProperty(value = "group_number")
    private Integer groupNumber;

    @JsonProperty(value = "about_self")
    private String aboutSelf;

    private String tags;

    private String contacts;

    private Boolean status;

    @JsonProperty(value = "current_team")
    private Team currentTeam;

    @JsonProperty(value = "track_id")
    private Long trackId;

    private Boolean captain;

    private String subscriptions;

    private User user;
}
