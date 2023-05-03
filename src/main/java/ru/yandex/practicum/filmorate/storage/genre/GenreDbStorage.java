package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component()
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String selectAllGenres = "SELECT * FROM PUBLIC.GENRE";
        return jdbcTemplate.query(selectAllGenres, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenre(long idGenre) {
        try {
            String selectGenreById = "SELECT * FROM PUBLIC.GENRE WHERE GENRE_ID=?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(selectGenreById, this::mapRowToGenre, idGenre));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getFilmGenres(Long idFilm) {
        try {
            String selectGenre = "SELECT G.GENRE_ID AS GENRE_ID, G.NAME AS NAME  " +
                    "FROM PUBLIC.FILM_GENRE FG LEFT JOIN PUBLIC.GENRE G ON FG.GENRE_ID = G.GENRE_ID " +
                    "WHERE FG.FILM_ID=? ORDER BY GENRE_ID";
            return jdbcTemplate.query(selectGenre, this::mapRowToGenre, idFilm);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public void addFilmGenre(long idFilm, long idGenre) {
        String insertGenre = "INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?,?)";
        jdbcTemplate.update(
                insertGenre,
                idFilm,
                idGenre);
    }

    @Override
    public void removeFilmAllGenre(long idFilm) {
        String deleteGenre = "DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID=?";
        jdbcTemplate.update(
                deleteGenre,
                idFilm);
    }

}
