package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpaList() {
        String getMpa = "select * from mpa";
        return jdbcTemplate.query(getMpa, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public Mpa findMpaById(int id) {
        String findMpaById = "select * from mpa where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(findMpaById, (rs, rowNum) -> mapRowToMpa(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("MPA rating with id {} not found", id);
            throw new NotFoundException(String.format("MPA rating with id %d not found", id));
        }
    }

    private Mpa mapRowToMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        return Mpa.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}
