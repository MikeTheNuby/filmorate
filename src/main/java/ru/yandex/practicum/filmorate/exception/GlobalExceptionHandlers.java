package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class GlobalExceptionHandlers {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Errors validationException(final ValidationException e) {
        return new Errors("Validation error" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Errors notFoundException(final NotFoundException e) {
        return new Errors("Data not found " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Errors serverErrorException(final Throwable e) {
        return new Errors("Internal Server Error" + e.getMessage());
    }
}
