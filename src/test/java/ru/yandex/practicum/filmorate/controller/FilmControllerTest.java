package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmService filmService;

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
        filmService = new FilmService();

    }

    @Test
    void findAllFilms() {
        assertEquals(0, filmService.findAllFilms().size());
        filmService.create(film1);
        assertEquals(1, filmService.findAllFilms().size());
        filmService.create(film2);
        assertEquals(2, filmService.findAllFilms().size());
        filmService.create(film3);
        assertEquals(3, filmService.findAllFilms().size());
    }

    @Test
    void shouldFilmCreate() {
        assertEquals(0, filmService.findAllFilms().size());
        filmService.create(film1);
        assertEquals(1, filmService.findAllFilms().size());
        assertEquals("Film-1", film1.getName());
    }

    @Test
    void shouldNotFilmCreateWithFailName() {
        film1.setName("");
        Assertions.assertThrows(ValidationException.class, () -> filmService.create(film1));
    }

    @Test
    void shouldNotFilmCreateWithFailDescription() {
        String longDescription = "Суперагент британской разведки Орсон Форчун наслаждался заслуженным " +
                "отпуском, когда родине срочно понадобились его услуги. Из секретной лаборатории " +
                "похищено предположительно смертельное оружие, , поэтому собрав команду из лучших " +
                "оперативников, Орсон выходит на посредника в готовящейся сделке продажи";
        film1.setDescription(longDescription);
        Assertions.assertThrows(ValidationException.class, () -> filmService.create(film1));
    }

    @Test
    void shouldNotFilmCreateWithFailReliesDate() {
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertThrows(ValidationException.class, () -> filmService.create(film1));
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
        Assertions.assertThrows(ValidationException.class, () -> filmService.create(film1));
        film2.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> filmService.create(film1));
        film3.setDuration(1);

        long duration = assertDoesNotThrow(() -> film3.getDuration());
        assertEquals(1, duration);
    }

/*    @Test
    void shouldUpdateFilm() {
        filmService.create(film1);
        assertEquals(1, filmService.findAllFilms().size());
        assertEquals("Film-1", filmService.findAllFilms().get(0).getName());
        film1.setName("newFilm-1");
        filmService.update(film1);
        assertEquals(1, filmService.findAllFilms().size());
        assertEquals("newFilm-1", filmService.findAllFilms().get(0).getName());
    }*/

/*    @Test
    void shouldNotFilmUpdateWithFailName() {
        film2.setName("");
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film2));
    }*/

/*    @Test
    void shouldNotFilmUpdateWithFailDescription() {
        filmService.create(film1);
        String longDescription = "Суперагент британской разведки Орсон Форчун наслаждался заслуженным " +
                "отпуском, когда родине срочно понадобились его услуги. Из секретной лаборатории " +
                "похищено предположительно смертельное оружие, , поэтому собрав команду из лучших " +
                "оперативников, Орсон выходит на посредника в готовящейся сделке продажи";
        film1.setDescription(longDescription);
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film1));
    }*/

/*    @Test
    void shouldNotFilmUpdateWithFailReliesDate() {
        filmService.create(film1);
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film1));
    }*/

/*    @Test
    void shouldNotFilmUpdateWithFailDuration() {
        filmService.create(film1);
        filmService.create(film2);
        filmService.create(film3);

        film1.setDuration(-50);
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film1));
        film2.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film2));

        film3.setDuration(1);
        filmService.update(film3);
        long duration = assertDoesNotThrow(() -> film3.getDuration());
        assertEquals(1, duration);
    }*/
}