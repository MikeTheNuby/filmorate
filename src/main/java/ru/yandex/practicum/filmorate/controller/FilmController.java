package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Validator validator = new Validator();
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @PostMapping
    public Film create(@NotNull @Valid @RequestBody Film film) {
        log.info("POST req received: {}", film);
        validator.filmValidator(film);
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Film updated");
        return film;
    }

    @PutMapping
    public Film update(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT req received: {}", film);

        if (!films.containsKey(film.getId())) {
            log.error("Film does not exists");
            throw new ValidationException("Film does not exists");
        }
        validator.filmValidator(film);
        log.info("Film updated");
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        log.info("{} films has been saved", films.size());
        return new ArrayList<>(films.values());
    }

}