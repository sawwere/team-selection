package ru.sfedu.teamselection.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для хранения информации об ошибках валидации данных.
 */
@Schema(description = "Validation error object")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstraintViolation {
    /**
     * Название поля, при валидации которого произошла ошибка
     */
    @Schema(description = "Name of the validated field")
    private String field;

    /**
     * Сообщение сопутствующее ошибке
     */
    @Schema(description = "Error response")
    private String message;

    /**
     * Возвращет строковое представление объекта.
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "{"
                + "field='" + field + '\''
                + ", message='" + message + '\''
                + '}';
    }
}
