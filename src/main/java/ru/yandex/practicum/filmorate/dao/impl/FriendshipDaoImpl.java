package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;

import java.util.List;

@Repository
public class FriendshipDaoImpl implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> getFriendsByUser(long id) {
        String getFriendsByUserQuery = "SELECT friend_id FROM friendship WHERE user_id = ? AND status = true " +
                "UNION SELECT user_id FROM friendship WHERE friend_id = ?";
        return jdbcTemplate.query(getFriendsByUserQuery, (resultSet, i) -> resultSet.getLong("friend_id"), id, id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String addFriend = "INSERT INTO friendship(user_id, friend_id, status) " +
                "VALUES (?, ?, false)";
        jdbcTemplate.update(addFriend, friendId, userId);
    }

    @Override
    public boolean updateFriend(long userId, long friendId, boolean status) {
        String updateFriendQuery = "UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?";
        int rowsAffected = jdbcTemplate.update(updateFriendQuery, status, userId, friendId);
        return rowsAffected > 0;
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE from friendship WHERE (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }
}