package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component()
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {

        String selectAllGenres = "SELECT * FROM PUBLIC.RATING";
        return jdbcTemplate.query(selectAllGenres, this::mapRowToEntity);
    }

    public Optional<Mpa> getMpa(long idMpa) {
        try {
            String selectGenreById = "SELECT * FROM PUBLIC.RATING WHERE RATING_ID=?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(selectGenreById, this::mapRowToEntity, idMpa));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Mpa mapRowToEntity(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}