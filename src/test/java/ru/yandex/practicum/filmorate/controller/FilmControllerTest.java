package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {

    FilmService filmService;
    InMemoryFilmStorage filmStorage;
    InMemoryUserStorage userStorage;
    Validator validator;

    Film film1 = new Film(1, "Film-1", "Good movie-1",
            LocalDate.of(2003, 12, 11), 120
    );
    Film film2 = new Film(2, "Film-2", "Good movie-2",
            LocalDate.of(2000, 10, 8), 64
    );
    Film film3 = new Film(3, "Film-3", "Good movie-3",
            LocalDate.of(1998, 5, 18), 264
    );

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
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        validator = new Validator();
        filmService = new FilmService(filmStorage, userStorage, validator);

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

    @Test
    void shouldUpdateFilm() {
        filmService.create(film1);
        assertEquals(1, filmService.findAllFilms().size());
        assertEquals("Film-1", filmService.findAllFilms().get(0).getName());
        film1.setName("newFilm-1");
        filmService.update(film1);
        assertEquals(1, filmService.findAllFilms().size());
        assertEquals("newFilm-1", filmService.findAllFilms().get(0).getName());
    }

    @Test
    void shouldNotFilmUpdateWithFailName() {
        film2.setName("");
        Assertions.assertThrows(NotFoundException.class, () -> filmService.update(film2));
    }

    @Test
    void shouldNotFilmUpdateWithFailDescription() {
        filmService.create(film1);
        String longDescription = "Суперагент британской разведки Орсон Форчун наслаждался заслуженным " +
                "отпуском, когда родине срочно понадобились его услуги. Из секретной лаборатории " +
                "похищено предположительно смертельное оружие, , поэтому собрав команду из лучших " +
                "оперативников, Орсон выходит на посредника в готовящейся сделке продажи";
        film1.setDescription(longDescription);
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film1));
    }

    @Test
    void shouldNotFilmUpdateWithFailReliesDate() {
        filmService.create(film1);
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        Assertions.assertThrows(ValidationException.class, () -> filmService.update(film1));
    }

    @Test
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
    }

    @Test
    void shouldGetPopularFilms() {
        filmService.create(film1);
        filmService.create(film2);
        filmService.create(film3);
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        filmService.addLike(film3.getId(), user1.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film3.getId(), user3.getId());

        assertEquals(3, film3.getLikes().size());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());

        assertEquals(2, film1.getLikes().size());

        filmService.addLike(film2.getId(), user1.getId());

        assertEquals(1, film2.getLikes().size());

        List<Film> popularFilms = filmService.getPopularFilms(3).getBody();

        assert popularFilms != null;
        assertEquals(3, popularFilms.size());
        assertEquals(film3, popularFilms.get(0));
        assertEquals(film1, popularFilms.get(1));
        assertEquals(film2, popularFilms.get(2));
    }

    @Test
    void shouldGetFilmById() {
        assertEquals(0, filmService.findAllFilms().size());
        filmService.create(film1);
        assertEquals(1, filmService.findAllFilms().size());
        assertEquals(1, Objects.requireNonNull(filmService.getFilmById(film1.getId()).getBody()).getId());
        assertEquals("Film-1", Objects.requireNonNull(filmService.getFilmById(film1.getId()).getBody()).getName());
    }

    @Test
    void shouldNotGetFilmByFailId() {
        assertEquals(0, filmService.findAllFilms().size());
        int failFilmId = 1000;
        Assertions.assertThrows(NotFoundException.class, () -> filmService.getFilmById(failFilmId));
    }

    @Test
    void shouldAddLikeToFilm() {
        filmService.create(film1);
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        assertEquals(0, film1.getLikes().size());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        assertEquals(3, film1.getLikes().size());
    }

    @Test
    void shouldRemoveLikeToFilm() {
        filmService.create(film1);
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        assertEquals(0, film1.getLikes().size());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        assertEquals(3, film1.getLikes().size());

        filmService.removeLike(film1.getId(), user1.getId());
        assertEquals(2, film1.getLikes().size());

        filmService.removeLike(film1.getId(), user2.getId());
        assertEquals(1, film1.getLikes().size());

        filmService.removeLike(film1.getId(), user3.getId());
        assertEquals(0, film1.getLikes().size());
    }

    @Test
    void shouldNotRemoveLikeToFilmFromUnknownUser() {
        filmService.create(film1);
        userStorage.create(user1);
        filmService.addLike(film1.getId(), user1.getId());
        int failUserId = 1000;

        assertEquals(1, film1.getLikes().size());
        Assertions.assertThrows(NotFoundException.class, () -> filmService.removeLike(film1.getId(), failUserId));
        assertEquals(1, film1.getLikes().size());
    }
}