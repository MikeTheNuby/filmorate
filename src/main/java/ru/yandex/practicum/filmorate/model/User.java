package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class User {

    String name;
    @Min(value = 0, message = "Значение id не может быть отрицательным.")
    int id;
    @Email(message = "Формат @mail не соответствует требованиям.")
    String email;
    @NotNull(message = "Логин не может быть пустым.")
    String login;
    @NotNull(message = "Дата рождения не может быть пустой.")
    @Past(message = "Дата рождения не может быть в будущем времени.")
    LocalDate birthday;
}