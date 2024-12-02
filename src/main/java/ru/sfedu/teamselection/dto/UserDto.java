package ru.sfedu.teamselection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link ru.sfedu.teamselection.domain.User}
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Min(1)
    private Long id;

    @NotBlank
    private String fio;

    @NotBlank
    private String email;

    @NotBlank
    private String role;

    @JsonProperty(value = "is_remind_enabled")
    @NotNull
    @Builder.Default
    private Boolean isRemindEnabled = Boolean.TRUE;

    @JsonProperty(value = "is_enabled")
    @NotNull
    @Builder.Default
    private Boolean isEnabled = Boolean.TRUE;
}
