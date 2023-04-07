package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Validator validator;
    private int id = 0;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, UserStorage userStorage, Validator validator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validator = validator;
    }

    public List<Film> findAllFilms() {
        log.info("{} films has been saved", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film create(Film film) {
        log.info("POST req received: {}", film);
        validator.filmValidate(film);
        id++;
        film.setId(id);
        filmStorage.create(film);
        log.info("Film created");
        return film;
    }

    public Film update(Film film) {
        log.info("PUT req received: {}", film);

        if (!filmStorage.getFilms().containsKey(film.getId())) {
            log.error("Film does not exists");
            throw new ValidationException("Film does not exists");
        }
        validator.filmValidate(film);
        log.info("Film updated");
        filmStorage.getFilms().put(film.getId(), film);
        return film;
    }

    public ResponseEntity<Film> addLike(int id, int userId) {
        Film film = filmStorage.getFilms().get(id);
        User user = userStorage.getUsers().get(userId);

        if (film != null && user != null) {
            film.getLikes().add(userId);
            log.info("User {} add like to {} film", user, film);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.info("User {} or film {} not found", id, userId);
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
    }
}

 /*
PUT /films/{id}/like/{userId}  — пользователь ставит лайк фильму.
DELETE /films/{id}/like/{userId}  — пользователь удаляет лайк.
GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
Если значение параметра count не задано, верните первые 10.
 */