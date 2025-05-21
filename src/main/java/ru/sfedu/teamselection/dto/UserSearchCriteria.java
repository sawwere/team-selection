package ru.sfedu.teamselection.dto;

import lombok.*;

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
