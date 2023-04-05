package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    FilmService filmService = new FilmService();

    @PostMapping
    public Film create(@NotNull @Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@NotNull @Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }
}