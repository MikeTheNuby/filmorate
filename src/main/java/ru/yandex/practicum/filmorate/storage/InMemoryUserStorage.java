package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public List<User> findAllUsers() {
        log.debug("Users size: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public void create(User user) {
        users.put(user.getId(), user);
        log.debug("User {} was created", user.getName());
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
        log.debug("User {} was updated", user.getName());
    }
}
