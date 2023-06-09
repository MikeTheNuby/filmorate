package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film getFilmById(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(Integer count);

    List<Long> addLike(long filmId, long userId);

    List<Long> deleteLike(long filmId, long userId);
}