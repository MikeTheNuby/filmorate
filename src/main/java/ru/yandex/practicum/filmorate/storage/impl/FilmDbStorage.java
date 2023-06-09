package ru.yandex.practicum.filmorate.storage.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.AbstractDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository("FilmDbStorage")
@Slf4j
public class FilmDbStorage extends AbstractDao implements FilmStorage {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Film> getAllFilms() {
        String getAllFilms = "SELECT f.*, m.name AS mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(getAllFilms, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Film getFilmById(long id) {
        String getFilmById = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        try {
            return jdbcTemplate.queryForObject(getFilmById, (rs, rowNum) -> mapRowToFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Movie with id {} not found.", id);
            throw new NotFoundException(String.format("Movie with id %d not found.", id));
        }
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getName().isEmpty()) {
            throw new IllegalArgumentException("Title missing.");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        film.getGenres().forEach(genre -> addGenreToFilm(id, genre.getId()));
        try {
            log.debug("Movie {} saved", objectMapper.writeValueAsString(film));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String updateFilm = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        if (jdbcTemplate.update(
                updateFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        ) > 0) {
            clearGenresFromFilm(film.getId());
            film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
            return film;
        }
        log.warn("Movie with id {} not found.", film.getId());
        throw new NotFoundException(String.format("Movie with id %d not found.", film.getId()));
    }

    public List<Film> getPopularFilms(int count) {
        String getPopularFilms = "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " + "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) AS likes_qty FROM likes GROUP BY film_id " +
                "ORDER BY likes_qty DESC limit ?) " + "AS top ON f.film_id = top.film_id " +
                "ORDER BY top.likes_qty DESC " + "limit ?";
        return jdbcTemplate.query(getPopularFilms, (rs, rowNum) -> mapRowToFilm(rs), count, count);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        String addGenreToFilm = "INSERT INTO film_genre(film_id, genre_id) " + "VALUES (?, ?)";
        jdbcTemplate.update(addGenreToFilm, filmId, genreId);
    }

    @Override
    public void deleteGenreFromFilm(long filmId, int genreId) {
        String deleteGenreFromFilm = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.update(deleteGenreFromFilm, filmId, genreId);
    }

    @Override
    public void clearGenresFromFilm(long filmId) {
        String clearGenresFromFilm = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(clearGenresFromFilm, filmId);
    }

    @Override
    public List<Long> getLikesByFilm(long filmId) {
        String getLikesByFilm = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(getLikesByFilm, Long.class, filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        String addLike = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(addLike, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String deleteLike = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteLike, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");

        Mpa mpa = Mpa.builder()
                .id(mpaId)
                .name(mpaName)
                .build();
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .build();
    }
}