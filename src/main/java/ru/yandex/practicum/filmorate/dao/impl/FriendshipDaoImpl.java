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
        String getFriendsByUser = "select friend_id from friendship where user_id =? and status = true " +
                "union select user_id from friendship where friend_id = ?";
        return jdbcTemplate.query(getFriendsByUser, (rs, rowNum) -> rs.getLong("friend_id"), id, id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String addFriend = "insert into friendship(user_id, friend_id, status) " +
                "values (?, ?, false)";
        jdbcTemplate.update(addFriend, friendId, userId);
    }

    @Override
    public boolean updateFriend(long userId, long friendId, boolean status) {
        String updateFriend = "update friendship set status = ? " +
                "where user_id = ? and friend_id = ?";
        return jdbcTemplate.update(updateFriend, status, userId, friendId) > 0;
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "delete from friendship where (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }
}
