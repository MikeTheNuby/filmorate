package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    List<Long> getLikesByFilm(long filmId);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long id);

    void deleteGenreFromFilm(long filmId, int genreId);

    void clearGenresFromFilm(long filmId);

    void addGenreToFilm(long filmId, int genreId);

    void deleteLike(long filmId, long userId);

    void addLike(long filmId, long userId);
}
