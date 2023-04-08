package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

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

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilm(
            @RequestParam(required = false,
                    defaultValue = "10", name = "count") int count
    ) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    ResponseEntity<Film> getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }
}