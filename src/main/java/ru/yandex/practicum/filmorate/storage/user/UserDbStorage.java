package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;


@Slf4j
@Component()
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    
    private final String UPDATE_USER =
            "UPDATE PUBLIC.USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE USER_ID=?";
    
    private final String SELECT_USER_BY_ID = "SELECT * FROM PUBLIC.USERS WHERE USER_ID=?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_USER);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            ps.setLong(5, user.getId());
            return ps;
        });
        return user;
    }
    @Override
    public User getUser(Long id) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_ID, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public List<User> findAll() {
        String SELECT_ALL_USER = "SELECT * FROM PUBLIC.USERS";
        return jdbcTemplate.query(SELECT_ALL_USER, this::mapRowToUser);
    }

    @Override
    public Boolean contains(User user) {
        var sqlRowSet = jdbcTemplate.queryForRowSet(SELECT_USER_BY_ID, user.getId());
        return sqlRowSet.isBeforeFirst();
    }

    @Override
    public Boolean contains(Long id) {
        var sqlRowSet = jdbcTemplate.queryForRowSet(SELECT_USER_BY_ID, id);
        return sqlRowSet.isBeforeFirst();
    }
}
