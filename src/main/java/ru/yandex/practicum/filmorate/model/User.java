package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class User {

    @Min(value = 0, message = "Значение id не может быть отрицательным.")
    private long id;
    private String name;
    @NotBlank(message = "Логин не может быть пустым.")
    private String login;
    @Email(message = "Формат @mail не соответствует требованиям.")
    private String email;
    @NotBlank(message = "Дата рождения не может быть пустой.")
    @Past(message = "Дата рождения не может быть в будущем времени.")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}