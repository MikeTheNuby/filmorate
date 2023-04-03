package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final List<String> userMails = new ArrayList<>();
    private final Validator validator = new Validator();
    private int id = 0;

    @Override
    public List<User> findAllUsers() {
        log.debug("List size: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        log.info("Post req received: {}", user);

        if (userMails.contains(user.getEmail())) {
            log.error("User already exists");
            throw new ValidationException("User already exists");
        }

        validator.userValidate(user);
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userMails.add(user.getEmail());
        log.info("User added {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Key not found : {}", user.getId());
            throw new ValidationException("Key not found.");
        }

        if (!users.containsValue(user)) {
            users.put(user.getId(), user);
        }
        validator.userValidate(user);
        log.debug("List size: {}", users.size());
        return user;
    }
}