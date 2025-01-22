package ru.sfedu.teamselection.dto.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для передачи данных об ошибках.
 */
@Schema(description = "Default error response")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo {
    /**
     * Строковое представление ошибки
     */
    @Schema(description = "Error name")
    private String error;

    /**
     *  * Описание ошибки
     */
    @Schema(description = "Error description")
    private String description;

    /**
     * Список ошибок валидации.
     * Используется для передачи информации об ошибках соответствующего типа.
     * В остальных случаях игнорируется и не передается клиенту
     */
    @Schema(description = "List of validation errors. Might not exists in case there are no such errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("constraint_violations")
    private List<ConstraintViolation> constraintViolations;
}
