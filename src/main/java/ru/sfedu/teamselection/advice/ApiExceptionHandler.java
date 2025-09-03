package ru.sfedu.teamselection.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintDeclarationException;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sfedu.teamselection.dto.ErrorResponse;
import ru.sfedu.teamselection.exception.BusinessException;
import ru.sfedu.teamselection.exception.ForbiddenException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoSuchElementException ex, HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, req);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            ConstraintDeclarationException.class,
            BusinessException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            RuntimeException ex, HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, req);
    }

    @ExceptionHandler(value = { ForbiddenException.class, AccessDeniedException.class })
    public ResponseEntity<ErrorResponse> handleAccessDeniedRequest(
            AccessDeniedException ex, HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(
            Exception ex, HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, req);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, Exception ex, HttpServletRequest req
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
