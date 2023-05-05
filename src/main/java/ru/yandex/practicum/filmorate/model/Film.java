package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {
    @Min(value = 0, message = "id value cannot be negative.")
    private long id;
    @NotBlank(message = "Title missing.")
    private String name;
    @Size(min = 1, max = 200, message = "Description length must be between 1 and 200 characters.")
    private String description;
    @NotNull(message = "Missing release date.")
    @ReleaseConstraint
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Movie duration is negative.")
    private int duration;
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Long> likes = new HashSet<>();

    public List<Genre> getGenres() {
        return genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
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
        values.put("mpa_id", mpa.getId());
        return values;
    }
}