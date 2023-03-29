package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final List<String> userMails = new ArrayList<>();
    private final Validator validator = new Validator();
    private static final LocalDate currentDate = LocalDate.now();

    public static LocalDate getCurrentDate() {
        return currentDate;
    }

    private int id = 0;

    @RequestMapping
    public List<User> findAllUsers() {
        log.debug("List size: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@NotNull @Valid @RequestBody User user) {
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

    @PutMapping
    public User update(@NotNull @Valid @RequestBody User user) {

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