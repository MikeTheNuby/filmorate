package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;
    private final Validator validator;
    private long id = 0;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage, Validator validator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validator = validator;
    }

    public List<Film> findAllFilms() {
        log.debug("{} films was saved", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film create(Film film) {
        validator.filmValidate(film);
        id++;
        film.setId(id);
        filmStorage.create(film);
        log.debug("Film {} created", film.getName());
        return film;
    }

    public Film update(Film film) {

        if (!filmStorage.getFilms().containsKey(film.getId())) {
            log.error("Film {} does not exists", film.getName());
            throw new ValidationException("Film does not exists");
        }
        validator.filmValidate(film);
        log.debug("Film {} updated", film.getName());
        filmStorage.getFilms().put(film.getId(), film);
        return film;
    }

    public ResponseEntity<Film> addLike(long id, long userId) {
        Film film = filmStorage.getFilms().get(id);
        User user = userStorage.getUsers().get(userId);

        if (film != null && user != null) {
            film.getLikes().add(userId);
            log.debug("User {} add like to {} film", user, film);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.debug("User {} or film {} not found", id, userId);
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Film> removeLike(long id, long userId) {
        Film film = filmStorage.getFilms().get(id);
        User user = userStorage.getUsers().get(userId);

        if (film != null && user != null) {
            film.getLikes().remove(userId);
            log.debug("User {} deleted like to {} film", user, film);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.debug("User {} or film {} not found", id, userId);
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<Film>> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.findAllFilms());
        films.sort(Film::compareTo);
        Collections.reverse(films);
        count = Math.min(count, films.size());
        log.debug("List of popular films created");
        return new ResponseEntity<>(films.subList(0, count), HttpStatus.OK);
    }

    public ResponseEntity<Film> getFilmById(@PathVariable long id) {
        if (filmStorage.getFilms().containsKey(id)) {
            Film film = filmStorage.getFilms().get(id);
            log.debug("Film id {} was found", id);
            return new ResponseEntity<>(film, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}