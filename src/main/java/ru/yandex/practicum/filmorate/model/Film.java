package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Film {

    String name;
    @Min(value = 0, message = "Значение id не может быть отрицательным.")
    int id;
    @Size(max = 200, message = "Размер описания превышает допустимый.")
    String description;
    @PastOrPresent
    LocalDate releaseDate;
    @Positive(message = "Длительность фильма не может быть отрицательной.")
    long duration;
}