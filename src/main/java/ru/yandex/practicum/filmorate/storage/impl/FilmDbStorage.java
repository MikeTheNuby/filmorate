package ru.yandex.practicum.filmorate.storage.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
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
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Film findFilmById(long id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToFilm(rs), id);
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
        String sql = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        if (jdbcTemplate.update(
                sql,
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
        String sql = "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " + "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) AS likes_qty FROM likes GROUP BY film_id " +
                "ORDER BY likes_qty DESC limit ?) " + "AS top ON f.film_id = top.film_id " +
                "ORDER BY top.likes_qty DESC " + "limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), count, count);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO film_genre(film_id, genre_id) " + "VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void deleteGenreFromFilm(long filmId, int genreId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void clearGenresFromFilm(long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Long> getLikesByFilm(long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
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