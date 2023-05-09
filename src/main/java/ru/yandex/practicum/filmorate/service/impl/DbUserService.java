package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service("DbUserService")
@Slf4j
public class DbUserService implements UserService {

    private final UserStorage storage;
    private final FriendshipDao friendshipDao;

    public DbUserService(@Qualifier("UserDbStorage") UserStorage storage, FriendshipDao friendshipDao) {
        this.storage = storage;
        this.friendshipDao = friendshipDao;
    }

    @Override
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    @Override
    public User findUserById(long id) {
        return storage.findUserById(id);
    }

    @Override
    public User addUser(User user) {
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    @Override
    public List<User> getFriends(long id) {
        return friendshipDao.getFriendsByUser(id).stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        findUserById(id);
        findUserById(otherId);
        return friendshipDao.getFriendsByUser(id).stream()
                .filter(friendshipDao.getFriendsByUser(otherId)::contains)
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> addFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean isUserToFriend = friendshipDao.getFriendsByUser(id).contains(friendId);
        boolean isFriendToUser = friendshipDao.getFriendsByUser(friendId).contains(id);
        if (!isUserToFriend && !isFriendToUser) {
            friendshipDao.addFriend(id, friendId);
        } else if (isUserToFriend && !isFriendToUser) {
            friendshipDao.updateFriend(friendId, id, true);
        } else {
            log.debug("Repeated friend request from user with id {} to user with id {}.", id, friendId);
        }
        return friendshipDao.getFriendsByUser(id);
    }

    @Override
    public List<Long> deleteFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean isUserHasFriend = friendshipDao.getFriendsByUser(id).contains(friendId);
        boolean isFriendHasUser = friendshipDao.getFriendsByUser(friendId).contains(id);
        if (!isUserHasFriend) {
            log.warn("User c id {} is not a friend of user c id {}.", friendId, id);
            throw new NotFoundException(
                    String.format("User c id %d is not a friend of user c id %d.",
                            friendId, id
                    ));
        } else if (!isFriendHasUser) {
            friendshipDao.deleteFriend(friendId, id);
        } else {
            if (!friendshipDao.updateFriend(id, friendId, false)) {
                friendshipDao.deleteFriend(friendId, id);
                friendshipDao.addFriend(id, friendId);
            }
        }
        return friendshipDao.getFriendsByUser(id);
    }
}