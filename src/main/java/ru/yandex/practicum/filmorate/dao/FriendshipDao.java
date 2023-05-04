package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendshipDao {

    List<Long> getFriendsByUser(long id);

    void addFriend(long userId, long friendId);

    boolean updateFriend(long userId, long friendId, boolean status);

    void deleteFriend(long userId, long friendId);
}
