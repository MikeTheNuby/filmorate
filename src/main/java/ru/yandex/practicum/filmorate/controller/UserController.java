package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public User create(@NotNull @Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@NotNull @Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @RequestMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getFriendsList(@PathVariable long id) {
        return userService.getFriendsList(id);
    }

    @RequestMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriendsList(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriendsList(id, otherId);
    }

    @GetMapping("/{id}")
    ResponseEntity<User> getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }
}