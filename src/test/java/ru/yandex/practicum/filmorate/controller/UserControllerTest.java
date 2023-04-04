package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    UserController controller;
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
        controller = new UserController();
    }

    @Test
    void shouldFindAllUsers() {
        assertEquals(0, controller.findAllUsers().size());
        controller.create(user1);
        assertEquals(1, controller.findAllUsers().size());
        controller.create(user2);
        assertEquals(2, controller.findAllUsers().size());
        controller.create(user3);
        assertEquals(3, controller.findAllUsers().size());
    }

    @Test
    void shouldUserCreate() {
        assertEquals(0, controller.findAllUsers().size());
        controller.create(user1);
        assertEquals(1, controller.findAllUsers().size());
        assertEquals("User-1", user1.getName());
    }

    @Test
    void shouldUserWithEmptyNameCreate() {
        user1.setName("");
        assertEquals("", user1.getName());
        assertEquals("login-1", user1.getLogin());

        controller.create(user1);
        assertEquals(1, controller.findAllUsers().size());
        assertEquals("login-1", user1.getName());
    }

    @Test
    void shouldNotUserCreateUnknownUser() {
        user1.setName("");
        user1.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(user1));
    }

    @Test
    void shouldNotUserCreateWithFailLogin() {
        user1.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(user1));
    }

    @Test
    void shouldNotUserCreateWithFailEmail() {
        user1.setEmail("");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(user1));
    }

    @Test
    void shouldNotUserCreateWithFailBirthDay() {
        user1.setBirthday(LocalDate.of(2980, 4, 12));
        Assertions.assertThrows(ValidationException.class, () -> controller.create(user1));
    }

    @Test
    void shouldUpdateUser() {
        controller.create(user1);
        assertEquals(1, controller.findAllUsers().size());
        assertEquals("User-1", controller.findAllUsers().get(0).getName());
        user1.setName("newUser-1");
        controller.update(user1);
        assertEquals(1, controller.findAllUsers().size());
        assertEquals("newUser-1", controller.findAllUsers().get(0).getName());
    }

    @Test
    void shouldNotUserUpdateUnknownUser() {
        user2.setName("");
        user2.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> controller.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailLogin() {
        user2.setLogin("");
        Assertions.assertThrows(ValidationException.class, () -> controller.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailEmail() {
        user2.setEmail("");
        Assertions.assertThrows(ValidationException.class, () -> controller.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailBirthDay() {
        user2.setBirthday(LocalDate.of(2980, 4, 12));
        Assertions.assertThrows(ValidationException.class, () -> controller.update(user2));
    }
}