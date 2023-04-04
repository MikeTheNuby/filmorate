package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController controller;
    Film film1 = new Film(1, "Film-1", "Good movie-1",
            LocalDate.of(2003, 12, 11), 120
    );
    Film film2 = new Film(2, "Film-2", "Good movie-2",
            LocalDate.of(2000, 10, 8), 64
    );
    Film film3 = new Film(3, "Film-3", "Good movie-3",
            LocalDate.of(1998, 5, 18), 264
    );

    @BeforeEach
    void setUp() {
        controller = new FilmController();

    }

    @Test
    void findAllFilms() {
        assertEquals(0, controller.findAllFilms().size());
        controller.create(film1);
        assertEquals(1, controller.findAllFilms().size());
        controller.create(film2);
        assertEquals(2, controller.findAllFilms().size());
        controller.create(film3);
        assertEquals(3, controller.findAllFilms().size());
    }

    @Test
    void shouldFilmCreate() {
        assertEquals(0, controller.findAllFilms().size());
        controller.create(film1);
        assertEquals(1, controller.findAllFilms().size());
        assertEquals("Film-1", film1.getName());
    }

    @Test
    void shouldNotFilmCreateWithFailName() {
        film1.setName("");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film1));
    }

    @Test
    void shouldNotFilmCreateWithFailDescription() {
        String longDescription = "Суперагент британской разведки Орсон Форчун наслаждался заслуженным " +
                "отпуском, когда родине срочно понадобились его услуги. Из секретной лаборатории " +
                "похищено предположительно смертельное оружие, , поэтому собрав команду из лучших " +
                "оперативников, Орсон выходит на посредника в готовящейся сделке продажи";
        film1.setDescription(longDescription);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film1));
    }

    @Test
    void shouldNotFilmCreateWithFailReliesDate() {
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film1));
    }

    @Test
    void shouldNotThrowFailReliesDateException() {
        film1.setReleaseDate(LocalDate.of(1895, 12, 28));
        LocalDate date = assertDoesNotThrow(() -> film1.getReleaseDate());
        assertEquals(LocalDate.of(1895, 12, 28), date);
    }

    @Test
    void shouldNotFilmCreateWithFailDuration() {
        film1.setDuration(-50);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film1));
        film2.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film1));
        film3.setDuration(1);

        long duration = assertDoesNotThrow(() -> film3.getDuration());
        assertEquals(1, duration);
    }

    @Test
    void shouldUpdateFilm() {
        controller.create(film1);
        assertEquals(1, controller.findAllFilms().size());
        assertEquals("Film-1", controller.findAllFilms().get(0).getName());
        film1.setName("newFilm-1");
        controller.update(film1);
        assertEquals(1, controller.findAllFilms().size());
        assertEquals("newFilm-1", controller.findAllFilms().get(0).getName());
    }

    @Test
    void shouldNotFilmUpdateWithFailName() {
        film2.setName("");
        Assertions.assertThrows(ValidationException.class, () -> controller.update(film2));
    }

    @Test
    void shouldNotFilmUpdateWithFailDescription() {
        controller.create(film1);
        String longDescription = "Суперагент британской разведки Орсон Форчун наслаждался заслуженным " +
                "отпуском, когда родине срочно понадобились его услуги. Из секретной лаборатории " +
                "похищено предположительно смертельное оружие, , поэтому собрав команду из лучших " +
                "оперативников, Орсон выходит на посредника в готовящейся сделке продажи";
        film1.setDescription(longDescription);
        Assertions.assertThrows(ValidationException.class, () -> controller.update(film1));
    }

    @Test
    void shouldNotFilmUpdateWithFailReliesDate() {
        controller.create(film1);
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertThrows(ValidationException.class, () -> controller.update(film1));
    }

    @Test
    void shouldNotFilmUpdateWithFailDuration() {
        controller.create(film1);
        controller.create(film2);
        controller.create(film3);

        film1.setDuration(-50);
        Assertions.assertThrows(ValidationException.class, () -> controller.update(film1));
        film2.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> controller.update(film2));

        film3.setDuration(1);
        controller.update(film3);
        long duration = assertDoesNotThrow(() -> film3.getDuration());
        assertEquals(1, duration);
    }
}