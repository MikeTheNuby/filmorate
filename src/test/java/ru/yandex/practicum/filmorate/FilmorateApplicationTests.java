package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FilmorateApplicationTests {

    private UserController userController;
    private FilmController filmController;

    @BeforeEach
    public void start() {
        userController = new UserController();
        filmController = new FilmController();
    }

    @Test
    public void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(filmController).isNotNull();
    }
}