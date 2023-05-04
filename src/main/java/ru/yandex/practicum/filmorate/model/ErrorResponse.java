package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @lombok.Getter
    String error;

    Map<String, String> errorsValidation;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(Map<String, String> errorsValidation) {
        this.errorsValidation = errorsValidation;
    }

}
