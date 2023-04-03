package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Validator validator = new Validator();
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public List<Film> findAllFilms() {
        log.info("{} films has been saved", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        log.info("POST req received: {}", film);
        validator.filmValidate(film);
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Film updated");
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("PUT req received: {}", film);

        if (!films.containsKey(film.getId())) {
            log.error("Film does not exists");
            throw new ValidationException("Film does not exists");
        }
        validator.filmValidate(film);
        log.info("Film updated");
        films.put(film.getId(), film);
        return film;
    }
}
