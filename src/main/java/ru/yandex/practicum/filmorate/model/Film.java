package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Film implements Comparable<Film> {

    @Min(value = 0, message = "id value cannot be negative.")
    private long id;
    private String name;
    @Size(max = 200, message = "Description size is too large.")
    private String description;
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive(message = "Movie duration cannot be negative.")
    private long duration;
    private final Set<Long> likes = new HashSet<>();

    @Override
    public int compareTo(Film o) {
        return this.likes.size() - o.likes.size();
    }
}