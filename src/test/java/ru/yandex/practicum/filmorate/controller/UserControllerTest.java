package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    UserController userController;
    UserService userService;
    InMemoryUserStorage userStorage;

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
        userService = new UserService(userStorage);
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
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailLogin() {
        user2.setLogin("");
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailEmail() {
        user2.setEmail("");
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user2));
    }

    @Test
    void shouldNotUserUpdateWithFailBirthDay() {
        user2.setBirthday(LocalDate.of(2980, 4, 12));
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user2));
    }

    @Test
    void shouldGetUserById() {
        userController.create(user1);
        ResponseEntity<User> responseEntity = ResponseEntity.ok(user1);
        User testUser = responseEntity.getBody();
        User user = userController.getUserById(1).getBody();

        assert testUser != null;
        assertEquals(1, testUser.getId());
        assertEquals(1, userController.findAllUsers().size());
        assert user != null;
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.hashCode(), user.hashCode());
    }

    @Test
    void shouldNotGetUserWithUnknownId() {
        assertEquals(0, userController.findAllUsers().size());
        int failUserId = 1000;
        Assertions.assertThrows(NotFoundException.class, () -> userController.getUserById(failUserId));
    }

    @Test
    void shouldGetEmptyFriendsList() {
        userController.create(user1);
        assertNotEquals(null, user1.getFriends());
        assertEquals(0, user1.getFriends().size());
    }

    @Test
    void shouldAddFriends() {
        userController.create(user1);
        userController.create(user2);
        assertEquals(0, user1.getFriends().size());
        assertEquals(0, user2.getFriends().size());

        userController.addFriend(user1.getId(), user2.getId());

        assertEquals(1, user1.getFriends().size());
        assertEquals(1, user2.getFriends().size());

        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void shouldNotAddFriendsWithUnknownId() {
        userController.create(user1);
        assertEquals(0, user1.getFriends().size());
        long nonexistentId = -1;

        Assertions.assertThrows(NotFoundException.class, () -> userController.addFriend(user1.getId(), nonexistentId));
        assertEquals(0, user1.getFriends().size());
        assertFalse(user1.getFriends().contains(nonexistentId));
    }

    @Test
    void shouldGetFriendsList() {
        userController.create(user1);
        userController.create(user2);
        userController.create(user3);

        assertEquals(0, user1.getFriends().size());

        userController.addFriend(user1.getId(), user2.getId());
        userController.addFriend(user1.getId(), user3.getId());

        assertEquals(2, user1.getFriends().size());
        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user1.getFriends().contains(user3.getId()));
    }

    @Test
    void shouldReturnAnEmptyCommonFriendsList() {
        userController.create(user1);
        userController.create(user2);
        userController.addFriend(user1.getId(), user2.getId());

        assertEquals(1, user1.getFriends().size());
        assertEquals(1, user2.getFriends().size());

        ResponseEntity<Set<User>> userFriends = userController.getCommonFriendsList(user1.getId(), user2.getId());
        assertEquals(0, Objects.requireNonNull(userFriends.getBody()).size());
    }

    @Test
    void shouldReturnCommonFriendsList() {
        userController.create(user1);
        userController.create(user2);
        userController.create(user3);
        User commonFriend = user3;

        userController.addFriend(user1.getId(), user2.getId());
        userController.addFriend(user1.getId(), commonFriend.getId());
        userController.addFriend(user2.getId(), commonFriend.getId());

        assertEquals(2, user1.getFriends().size());
        assertEquals(2, user2.getFriends().size());
        assertEquals(2, user3.getFriends().size());

        ResponseEntity<Set<User>> userFriends = userController.getCommonFriendsList(user1.getId(), user2.getId());
        assertEquals(1, Objects.requireNonNull(userFriends.getBody()).size());
        assertTrue(Objects.requireNonNull(userFriends.getBody()).contains(commonFriend));
    }

    @Test
    void shouldRemoveFriend() {
        userController.create(user1);
        userController.create(user2);
        assertEquals(0, user1.getFriends().size());
        assertEquals(0, user2.getFriends().size());

        userController.addFriend(user1.getId(), user2.getId());

        assertEquals(1, user1.getFriends().size());
        assertEquals(1, user2.getFriends().size());

        userController.removeFriend(user1.getId(), user2.getId());

        assertEquals(0, user1.getFriends().size());
        assertEquals(0, user2.getFriends().size());
    }
}