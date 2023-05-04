package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    List<Genre> getGenres();

    List<Genre> getGenresByFilm(long id);

    Genre findGenreById(int id);
}
