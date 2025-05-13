package ru.sfedu.teamselection.dto;

import lombok.*;

@Getter
@Builder
public class ErrorResponse {
    /** Время ошибки в ISO-формате */
    private String timestamp;
    /** HTTP-код */
    private int status;
    /** Краткое описание статуса (например, "Bad Request") */
    private String error;
    /** Подробное сообщение исключения */
    private String message;
    /** Запрошенный путь */
    private String path;
}