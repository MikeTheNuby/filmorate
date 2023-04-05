package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface FilmStorage {

    void create(@NotNull @Valid @RequestBody Film film);

    void update(@NotNull @Valid @RequestBody Film film);

    List<Film> findAllFilms();
}
