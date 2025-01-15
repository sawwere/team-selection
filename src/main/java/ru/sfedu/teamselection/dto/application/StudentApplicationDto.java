package ru.sfedu.teamselection.dto.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sfedu.teamselection.domain.Student;

/**
 * DTO for {@link Student} field of the {@link ApplicationDto}
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentApplicationDto {
    private Long id;

    private String fio;

    private Integer course;

    @JsonProperty(value = "group_number")
    private Integer groupNumber;

    @JsonProperty(value = "about_self")
    @Size(max = 1024)
    private String aboutSelf;

    @Size(max = 255)
    private String contacts;

    @JsonProperty(value = "user_id")
    @NotNull
    private Long userId;
}
