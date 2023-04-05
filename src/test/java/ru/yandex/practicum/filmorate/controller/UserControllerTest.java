package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserControllerTest {

    UserController userController;
    UserService userService;
    UserStorage userStorage;
    Validator validator;

    User user1 = new User(1, "User-1", "login-1", "e487837708@fireboxmail.lol",
            LocalDate.of(1980, 4, 12)
    );
    User user2 = new User(2, "User-2", "login-2", "a52aeeb878@fireboxmail.lol",
            LocalDate.of(1992, 11, 22)
    );
    User user3 = new User(3, "User-3", "login-3", "cbad71c5ee@fireboxmail.lol",
            LocalDate.of(2001, 7, 7)
    );

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        validator = new Validator();
        userService = new UserService(userStorage, validator);
        userController = new UserController(userService);
    }

    @Test
    void shouldFindAllUsers() {
        assertEquals(0, userController.findAllUsers().size());
        userController.create(user1);
        assertEquals(1, userController.findAllUsers().size());
        userController.create(user2);
        assertEquals(2, userController.findAllUsers().size());
        userController.create(user3);
        assertEquals(3, userController.findAllUsers().size());
    }

    @Test
    void shouldUserCreate() {
        assertEquals(0, userController.findAllUsers().size());
        userController.create(user1);
        assertEquals(1, userController.findAllUsers().size());
        assertEquals("User-1", user1.getName());
    }

    @Test
    void shouldUserWithEmptyNameCreate() {
        user1.setName("");
        assertEquals("", user1.getName());
        assertEquals("login-1", user1.getLogin());

        userController.create(user1);
        assertEquals(1, userController.findAllUsers().size());
        assertEquals("login-1", user1.getName());
    }

    @Test
    void shouldNotUserCreateUnknownUser() {
        user1.setName("");
        user1.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    void shouldNotUserCreateWithFailLogin() {
        user1.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    void shouldNotUserCreateWithFailEmail() {
        user1.setEmail("");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    void shouldNotUserCreateWithFailBirthDay() {
        user1.setBirthday(LocalDate.of(2980, 4, 12));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    void shouldUpdateUser() {
        userController.create(user1);
        assertEquals(1, userController.findAllUsers().size());
        assertEquals("User-1", userController.findAllUsers().get(0).getName());
        user1.setName("newUser-1");
        userController.update(user1);
        assertEquals(1, userController.findAllUsers().size());
        assertEquals("newUser-1", userController.findAllUsers().get(0).getName());
    }

    @Test
    void shouldNotUserUpdateUnknownUser() {
        user2.setName("");
        user2.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> userController.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailLogin() {
        user2.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> userController.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailEmail() {
        user2.setEmail("");
        Assertions.assertThrows(ValidationException.class, () -> userController.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailBirthDay() {
        user2.setBirthday(LocalDate.of(2980, 4, 12));
        Assertions.assertThrows(ValidationException.class, () -> userController.update(user2));
    }
}