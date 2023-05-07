package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        String getGenres = "SELECT * FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(getGenres, (rs, rowNum) -> mapRowToGenre(rs));
    }

    @Override
    public Genre getGenreById(int id) {
        String getGenreById = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(getGenreById, (rs, rowNum) -> mapRowToGenre(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Genre with id {} not found.", id);
            throw new NotFoundException(String.format("Genre with id %d not found.", id));
        }
    }

    @Override
    public List<Genre> getGenresByFilm(long id) {
        String getGenresByFilm = "SELECT g.* FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id =? " +
                "ORDER BY g.genre_id";
        return jdbcTemplate.query(getGenresByFilm, (rs, rowNum) -> mapRowToGenre(rs), id);
    }

    private Genre mapRowToGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }
}