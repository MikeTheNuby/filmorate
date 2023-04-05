package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private final Validator validator = new Validator();
    private int id = 0;

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
}

 /*
 log.debug("List size: {}", users.size());
добавление и удаление лайка,
вывод 10 наиболее популярных фильмов по количеству лайков.
Пусть пока каждый пользователь может поставить лайк фильму только один раз.
 */