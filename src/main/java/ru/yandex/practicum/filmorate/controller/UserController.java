package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    InMemoryUserStorage userStorage = new InMemoryUserStorage();

    @RequestMapping
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User create(@NotNull @Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@NotNull @Valid @RequestBody User user) {
        return userStorage.update(user);

    }
}