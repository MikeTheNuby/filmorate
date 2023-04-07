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
public class Film {

    @Min(value = 0, message = "Значение id не может быть отрицательным.")
    private int id;
    private String name;
    @Size(max = 200, message = "Размер описания превышает допустимый.")
    private String description;
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма не может быть отрицательной.")
    private long duration;
    private final Set<Integer> likes = new HashSet<>();
}