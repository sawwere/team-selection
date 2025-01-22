package ru.sfedu.teamselection.exception;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sfedu.teamselection.dto.exception.ConstraintViolation;
import ru.sfedu.teamselection.dto.exception.ErrorInfo;

/**
 * Глобальный обработчик возникающих в процессе работы приложения исключений.
 * Служит для возвращение сообщений об ошибках в едином формате при их возникновении.
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Базовый обработчик для ловли непойманных другими методами исключений
     * @param ex возникшее исключение
     * @param request текущий запрос
     * @return ResponseEntity с телом, содержашим информацию об ошибке
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
        return handleException(ex, request);
    }

    /**
     * Обработчик ошибки валидации, возникающий при проверке аргументов помеченных аннотацией @Valid
     * @param ex the exception to handle
     * @param headers the headers to be written to the response
     * @param status the selected response status
     * @param request the current request
     * @return ResponseEntity с телом, содержашим информацию об ошибке в виде ErrorInfo
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        List<ConstraintViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(violation ->
                        new ConstraintViolation(
                                violation.getField(),
                                violation.getDefaultMessage()
                        )
                )
                .toList();
        return ResponseEntity.badRequest().body(
                ErrorInfo.builder()
                        .error("Validation error")
                        .description(ex.getObjectName())
                        .constraintViolations(violations)
                        .build()
        );
    }

    /**
     * Обработчик ошибки, возникающий при получении некорректного JSON объекта
     * @param ex the exception to handle
     * @param headers the headers to use for the response
     * @param status the status code to use for the response
     * @param request the current request
     * @return ResponseEntity с телом, содержашим информацию об ошибке в виде ErrorInfo
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(
                ErrorInfo.builder()
                        .error("Invalid JSON request")
                        .description(ex.getMessage())
                        .build()
        );
    }

    /**
     * Обработчик ошибки, возникающий при отстутствии обработчика на пользвательские запрос
     * @param ex the exception to handle
     * @param headers the headers to use for the response
     * @param status the status code to use for the response
     * @param request the current request
     * @return ResponseEntity с телом, содержашим информацию об ошибке в виде ErrorInfo
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(
                ErrorInfo.builder()
                        .error("No handler found")
                        .description(ex.getMessage())
                        .build()
        );
    }
}
