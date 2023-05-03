package ru.yandex.practicum.filmorate.dbTest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.storage.friends.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    private static UserDbStorage userStorage;
    private static FriendDbStorage friendStorage;

    @BeforeAll
    public static void setUp(
            @Autowired UserDbStorage userStorage,
            @Autowired FriendDbStorage friendStorage
    ) {
        setUserStorage(userStorage);
        setFriendStorageStorage(friendStorage);

        User user1 = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser = userStorage.create(user1);

        User user2 = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser2 = userStorage.create(user2);

        User user3 = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser3 = userStorage.create(user3);

        friendStorage.addFriend(createdUser.getId(), createdUser2.getId());
        friendStorage.addFriend(createdUser3.getId(), createdUser2.getId());
        friendStorage.addFriend(createdUser3.getId(), createdUser.getId());
    }

    public static void setUserStorage(UserDbStorage userStorage) {
        UserControllerTest.userStorage = userStorage;
    }

    public static void setFriendStorageStorage(FriendDbStorage friendStorage) {
        UserControllerTest.friendStorage = friendStorage;
    }

    @Test
    public void testCreateUser() {
        User user = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser = userStorage.create(user);
        assertThat(createdUser).hasFieldOrPropertyWithValue("id", user.getId());
    }

    @Test
    public void testFindUserById() {

        User user1 = userStorage.getUser(1L);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);

    }

    @Test
    public void testFindUserByWrongId() {

        final EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class,
                () -> userStorage.getUser(2100L)
        );
        assertEquals("Incorrect result size: expected 1, actual 0", exception.getMessage());

    }

    @Test
    public void testUpdateUser() {
        User user = User.builder()
                .id(1L)
                .email("email2")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User updatesUser = userStorage.update(user);
        assertThat(updatesUser).hasFieldOrPropertyWithValue("email", "email2");
    }

    @Test
    @Order(1)
    public void testGetAllUsers() {
        var users = userStorage.findAll();
        assertEquals(3, users.size(), "Invalid number of users.");
    }

    @Test
    public void testGetFriendship() {
        var users = friendStorage.getAllFriends(1L);
        assertEquals(1, users.size(), "Wrong number of friends.");
    }

    @Test
    public void testGetCommonFriends() {
        var users = friendStorage.getCommonFriends(1L, 3L);
        assertEquals(1, users.size(), "Wrong number of friends.");
    }

    @Test
    public void testRemoveFriendship() {
        friendStorage.removeFriend(3L, 1L);
        var users = friendStorage.getAllFriends(3L);
        assertEquals(1, users.size(), "Wrong number of friends.");
    }

    @Test
    void validateUserWithEmptyEmail() {
        User user = User.builder()
                .id(1L)
                .email("")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(user)
        );
        assertEquals("Invalid email", exception.getMessage());
    }

    @Test
    void validateUserWithEmptyLogin() {
        User user = User.builder()
                .id(1L)
                .email("email")
                .login("")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(user)
        );
        assertEquals("Login is empty or contains spaces", exception.getMessage());
    }

    @Test
    void validateUserWithBirthdayAfterNow() {
        User user = User.builder()
                .id(1L)
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().plusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(user)
        );
        assertEquals("Birthday is after now", exception.getMessage());
    }
}
