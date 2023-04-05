package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.controller.Validator;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FilmorateApplicationTests {

    UserController userController;
    UserService userService;
    UserStorage userStorage;
    Validator validator;

    @BeforeEach
    public void start() {
        userStorage = new InMemoryUserStorage();
        validator = new Validator();
        userService = new UserService(userStorage, validator);
        userController = new UserController(userService);
    }

    @Test
    public void contextLoads() {
        assertThat(userController).isNotNull();
        //assertThat(filmController).isNotNull();
    }
}