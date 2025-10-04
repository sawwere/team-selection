package ru.sfedu.teamselection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {
    private String fio;
    private String email;
    private String role;
    private Integer course;
    private Integer groupNumber;
    private Long    trackId;
    @Builder.Default
    private Boolean isEnabled = true;
}
