package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User findUserById(long id);

    User addUser(User user);

    User updateUser(User user);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

    List<Long> addFriend(long id, long friendId);

    List<Long> deleteFriend(long id, long friendId);
}
