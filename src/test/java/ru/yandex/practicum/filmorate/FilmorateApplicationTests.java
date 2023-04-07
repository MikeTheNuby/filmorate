package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FilmorateApplicationTests {

    FilmController filmController;
    UserController userController;

    public FilmorateApplicationTests(FilmController filmController, UserController userController) {
        this.filmController = filmController;
        this.userController = userController;
    }

    @Test
    public void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(filmController).isNotNull();
    }
}