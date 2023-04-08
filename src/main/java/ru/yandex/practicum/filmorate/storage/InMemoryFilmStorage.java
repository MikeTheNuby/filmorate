package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public List<Film> findAllFilms() {
        log.info("{} films has been saved", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public void create(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }
}
