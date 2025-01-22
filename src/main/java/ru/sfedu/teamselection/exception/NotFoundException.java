package ru.sfedu.teamselection.exception;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, генерируемое в случае попытки клиента произвести действие с отсутствующей в базе данных сущностью.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends NoSuchElementException {
    public NotFoundException(String message) {
        super(message);
    }
}
