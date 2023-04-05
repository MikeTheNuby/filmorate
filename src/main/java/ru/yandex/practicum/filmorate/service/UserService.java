package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final Validator validator;
    private Integer id = 0;

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
}

 /*
 log.debug("List size: {}", users.size());
добавление в друзья,
удаление из друзей,
вывод списка общих друзей.
в друзья — добавляем сразу.
Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
 */