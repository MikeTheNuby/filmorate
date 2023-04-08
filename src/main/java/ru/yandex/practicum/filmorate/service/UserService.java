package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserService {

    private final InMemoryUserStorage userStorage;
    private final Validator validator;
    private long id = 0;

    @Autowired
    public UserService(InMemoryUserStorage userStorage, Validator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User create(User user) {
        if (userStorage.getUserMails().contains(user.getEmail())) {
            log.error("User {} already exists", user.getName());
            throw new ValidationException("User already exists");
        }

        validator.userValidate(user);
        id++;
        user.setId(id);
        userStorage.create(user);
        log.debug("User {} added", user.getId());
        return user;
    }

    public ResponseEntity<User> update(User user) {
        if (userStorage.getUsers().containsKey(user.getId())) {
            userStorage.getUsers().put(user.getId(), user);
            validator.userValidate(user);
            validator.removeAbandonedEmails();
            log.debug("User {} data updated.", user.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        else {
            log.debug("Key {} not found", user.getId());
            throw new ValidationException("Key not found");
        }
    }

    public ResponseEntity<User> addFriend(long id, long friendId) {
        User user = userStorage.getUsers().get(id);
        User friend = userStorage.getUsers().get(friendId);

        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
            log.debug("User {} and user {} are now friends", user, friend);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            log.debug("User {} or {} not found", id, friendId);
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    public User removeFriend(long id, long friendId) {
        User user = userStorage.getUsers().get(id);
        User friend = userStorage.getUsers().get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.debug("User {} was removed from friends list.", friendId);
        return user;
    }

    public ResponseEntity<Set<User>> getFriendsList(long id) {
        if (userStorage.getUsers().containsKey(id)) {
            log.debug("Friends of user with {} received", id);
            Set<User> friends = new HashSet<>();
            for (long idFriends : userStorage.getUsers().get(id).getFriends()) {
                friends.add(userStorage.getUsers().get(idFriends));
            }
            return new ResponseEntity<>(friends, HttpStatus.OK);
        }
        else {
            log.debug("User {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Set<User>> getCommonFriendsList(long id, long otherId) {
        if (userStorage.getUsers().containsKey(id) & userStorage.getUsers().containsKey(otherId)) {
            Set<User> friends = new HashSet<>();
            Stream<Long> userStream = userStorage.getUsers().get(id).getFriends().stream();
            userStream.forEach((userId) -> {
                Stream<Long> otherUserStream = userStorage.getUsers().get(otherId).getFriends().stream();
                otherUserStream.forEach((otherUserId) -> {
                    if (Objects.equals(userId, otherUserId)) {
                        friends.add(userStorage.getUsers().get(userId));
                    }
                });
            });
            log.debug("Common friends of users id {} and id {} was received", id, otherId);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        }
        else {
            log.debug("User id {} or id {} not found", id, otherId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<User> getUserById(@PathVariable long id) {
        if (userStorage.getUsers().containsKey(id)) {
            User user = userStorage.getUsers().get(id);
            log.debug("User id {} was found", id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}