package ru.yandex.practicum.filmorate.storage.likes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component()
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmLike(Long idFilm, Long idUser) {
        String insertLike = "INSERT INTO PUBLIC.FILM_LIKE (FILM_ID, USER_ID) VALUES(?,?)";
        jdbcTemplate.update(
                insertLike,
                idFilm,
                idUser);
    }

    @Override
    public void removeFilmLike(Long idFilm, Long idUser) {
        String deleteLike = "DELETE FROM PUBLIC.FILM_LIKE  WHERE FILM_ID=? AND USER_ID=? ";
        jdbcTemplate.update(
                deleteLike,
                idFilm,
                idUser);
    }

    @Override
    public List<Long> getAllLikes(Long idFilm) {
        try {
            String selectLike = "SELECT USER_ID FROM PUBLIC.FILM_LIKE  WHERE FILM_ID=? ";
            return jdbcTemplate.queryForList(selectLike, Long.class, idFilm);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

}
