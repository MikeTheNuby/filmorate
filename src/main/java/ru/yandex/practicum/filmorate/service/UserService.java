package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

public class UserService {
    private final UserStorage userStorage;
    private final Validator validator;
    private Integer id = 0;

    @Autowired
    public UserService(UserStorage userStorage, Validator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    public List<User> findAllUsers() {
       return userStorage.findAllUsers();
    }

    public User create(User user) {
        log.info("Post req received: {}", user);

        if (userStorage.getUserMails().contains(user.getEmail())) {
            log.error("User already exists");
            throw new ValidationException("User already exists");
        }

        validator.userValidate(user);
        id++;
        user.setId(id);
        userStorage.create(user);
        log.info("User added {}", user);
        return user;
    }

    public User update(User user) {
        if (!userStorage.getUsers().containsKey(user.getId())) {
            log.debug("Key not found : {}", user.getId());
            throw new ValidationException("Key not found.");
        }

        if (!userStorage.getUsers().containsValue(user)) {
            userStorage.getUsers().put(user.getId(), user);
        }
        validator.userValidate(user);
        validator.removeAbandonedEmails();
        log.debug("List size: {}", userStorage.getUsers().size());
        return user;
    }

    public User addFriend(int id, int friendId){
        User user = userStorage.getUsers().get(id);
        User friend = userStorage.getUsers().get(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        return user;
    }

    public User removeFriend(int id, int friendId){
        User user = userStorage.getUsers().get(id);
        User friend = userStorage.getUsers().get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        return user;
    }
}

 /*
+ PUT /users/{id}/friends/{friendId}  — добавление в друзья.
+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
- GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
- GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем
 */