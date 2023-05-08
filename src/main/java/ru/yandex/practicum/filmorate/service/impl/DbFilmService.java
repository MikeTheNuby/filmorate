package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("DbFilmService")
@Slf4j
public class DbFilmService implements FilmService {

    private final FilmStorage storage;
    private final GenreDao genreDao;
    private final UserService userService;
    private final MpaDaoImpl mpaStorage;

    @Autowired
    public DbFilmService(
            @Qualifier("FilmDbStorage") FilmStorage storage,
            GenreDao genreDao,
            @Qualifier("DbUserService") UserService userService,
            MpaDaoImpl mpaStorage
    ) {
        this.storage = storage;
        this.genreDao = genreDao;
        this.userService = userService;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = storage.getAllFilms();

        for (Film film : films) {
            List<Genre> genres = genreDao.getGenresByFilm(film.getId());
            film.setGenres(genres);

            Mpa mpa = mpaStorage.findMpaById(film.getMpa().getId());
            film.setMpa(mpa);
        }

        return films;
    }

    @Override
    public Film getFilmById(long id) {
        Film film = storage.getFilmById(id);
        genreDao.getGenresByFilm(film.getId())
                .forEach(film::addGenre);
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return storage.getPopularFilms(count).stream()
                .peek(film -> genreDao.getGenresByFilm(film.getId())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> addLike(long filmId, long userId) {
        getFilmById(filmId);
        userService.findUserById(userId);
        storage.addLike(filmId, userId);
        return storage.getLikesByFilm(filmId);
    }

    @Override
    public List<Long> deleteLike(long filmId, long userId) {
        getFilmById(filmId);
        userService.findUserById(userId);
        storage.deleteLike(filmId, userId);
        return storage.getLikesByFilm(filmId);
    }
}