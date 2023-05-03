package ru.yandex.practicum.filmorate.dbTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerTest {

    private static FilmDbStorage filmStorage;
    private static MpaDbStorage mpaStorage;
    private static LikeDbStorage likeStorage;
    private static GenreDbStorage genreStorage;

    @BeforeAll
    public static void setUp(
            @Autowired UserDbStorage userStorage,
            @Autowired FriendDbStorage friendStorage,
            @Autowired FilmDbStorage filmDbStorage,
            @Autowired MpaDbStorage mpaStorage,
            @Autowired LikeDbStorage likeStorage,
            @Autowired GenreDbStorage genreStorage
    ) {
        setFilmStorage(filmDbStorage);
        setLikeStorage(likeStorage);
        setMpaStorage(mpaStorage);
        setGenreStorage(genreStorage);
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmDbStorage.create(film);

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

    public static void setFilmStorage(FilmDbStorage filmDbStorage) {
        FilmControllerTest.filmStorage = filmDbStorage;

    }

    public static void setMpaStorage(MpaDbStorage mpaStorage) {
        FilmControllerTest.mpaStorage = mpaStorage;
    }

    public static void setLikeStorage(LikeDbStorage likeStorage) {
        FilmControllerTest.likeStorage = likeStorage;
    }

    public static void setGenreStorage(GenreDbStorage genreStorage) {
        FilmControllerTest.genreStorage = genreStorage;
    }

    @Test
    public void shouldCreateFilm() {
        Film film = Film.builder()
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Film createdFilm = filmStorage.create(film);
        assertThat(createdFilm).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    public void shouldFindFilmById() {
        Film film1 = filmStorage.getFilm(1L);
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void shouldFindFilmByWrongId() {

        final EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class,
                () -> filmStorage.getFilm(2100L)
        );
        assertEquals("Incorrect result size: expected 1, actual 0", exception.getMessage());
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name3")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Film updatedFilm = filmStorage.update(film);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "name3");
    }

    @Test
    public void testGetAllFilms() {
        var films = filmStorage.findAll();
        assertEquals(1, films.size(), "Wrong number of movies.");
    }

    @Test
    public void shouldGetAllMpa() {
        var mpas = mpaStorage.getAllMpa();
        assertEquals(5, mpas.size(), "Wrong number of MPA.");
    }

    @Test
    public void shouldGetMpa() {
        var mpa = mpaStorage.getMpa(1);
        assertThat(mpa).isPresent().hasValueSatisfying(mpa1 ->
                assertThat(mpa1).hasFieldOrPropertyWithValue("name", "G")
        );
    }

    @Test
    public void shouldAddLike() {
        likeStorage.addFilmLike(1L, 1L);
        var likes = likeStorage.getAllLikes(1L);
        assertEquals(1, likes.size(), "Wrong number of likes.");
    }

    @Test
    public void shouldRemoveLike() {
        likeStorage.removeFilmLike(1L, 1L);
        var likes = likeStorage.getAllLikes(1L);
        assertEquals(0, likes.size(), "Wrong number of likes.");
    }

    @Test
    public void shouldAddGenre() {
        var genres = genreStorage.getAllGenres();
        assertEquals(6, genres.size(), "Wrong number of genres.");
    }

    @Test
    public void shouldAddMultipleGenres() {
        genreStorage.addFilmGenre(1L, 1L);
        genreStorage.addFilmGenre(1L, 2L);
        var genres = genreStorage.getFilmGenres(1L);
        assertEquals(2, genres.size(), "Wrong number of genres.");
    }

    @Test
    public void shouldRemoveGenre() {
        genreStorage.addFilmGenre(2L, 1L);
        genreStorage.removeFilmAllGenre(2L);
        var genres = genreStorage.getFilmGenres(2L);
        assertEquals(0, genres.size(), "Wrong number of genres.");
    }

    @Test
    void shouldNotFilmCreateWithFailName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(film)
        );
        assertEquals("Film name is empty", exception.getMessage());
    }

    @Test
    void shouldNotFilmCreateWithFailDescription() {
        String invalidLength = "British intelligence superspy Orson Fortune enjoyed a " +
                "well-deserved " +
                "a vacation when the homeland urgently needed his services. From a secret laboratory" +
                "a supposedly lethal weapon has been stolen, so we muster a team of the best" +
                "operatives, Orson is approaching an intermediary in the impending sale of";

        Film film = Film.builder()
                .id(1)
                .name("name")
                .description(invalidLength)
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(film)
        );
        assertEquals("Description is longer than 200", exception.getMessage());
    }

    @Test
    void shouldNotFilmCreateWithFailReliesDate() {

        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now().minusYears(200))
                .duration(1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(film)
        );
        assertEquals("Release date is before 28 december 1895", exception.getMessage());
    }

    @Test
    void shouldNotFilmCreateWithFailDuration() {

        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(-1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(film)
        );
        assertEquals("Duration is negative", exception.getMessage());
    }

}