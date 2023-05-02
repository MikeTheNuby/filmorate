package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class Film {

    @Min(value = 0, message = "Значение id не может быть отрицательным.")
    private long id;
    @NotNull
    private String name;
    @Size(max = 200, message = "Размер описания превышает допустимый.")
    private String description;
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма не может быть отрицательной.")
    private Long duration;
    @NotNull
    private Integer rate;
    @NotNull
    private Mpa mpa;
    private final Set<Long> likes = new TreeSet<>();
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));

    public void addLike(long idUser) {
        likes.add(idUser);
    }

    public void removeLike(long idUser) {
        likes.remove(idUser);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating_id", mpa == null ? null : mpa.getId());
        return values;
    }
}
