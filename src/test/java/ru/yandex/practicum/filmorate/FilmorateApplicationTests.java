package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FriendshipDao friendshipDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    User.UserBuilder userBuilder;
    Film.FilmBuilder filmBuilder;
    Genre.GenreBuilder genreBuilder;
    Mpa.MpaBuilder mpaBuilder;

    private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        userBuilder = User.builder()
                .email("e@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1985, 9, 7));

        mpaBuilder = Mpa.builder()
                .id(1);

        genreBuilder = Genre.builder()
                .id(1);

        filmBuilder = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(testReleaseDate)
                .duration(90)
                .mpa(mpaBuilder.build());
    }

    @AfterEach
    public void cleanDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "users", "films", "friendship", "film_genre", "likes");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void addUserTest() {
        User user = userBuilder.build();
        User addedUser = userStorage.addUser(user);
        assertThat(addedUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testFindUserById() {
        User user = userBuilder.build();
        User addedUser = userStorage.addUser(user);
        User foundUser = userStorage.findUserById(addedUser.getId());

        assertThat(foundUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", addedUser.getId())
                .isEqualTo(addedUser);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userStorage.findUserById(-1L));
        assertEquals("User with id -1 not found", ex.getMessage());

        ex = assertThrows(NotFoundException.class, () -> userStorage.findUserById(999L));
        assertEquals("User with id 999 not found", ex.getMessage());
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.getAllUsers();
        assertThat(users)
                .isNotNull()
                .isEqualTo(Collections.emptyList());

        User user = userBuilder.build();
        userStorage.addUser(user);
        users = userStorage.getAllUsers();
        assertNotNull(users);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getId(), 1);
    }

    @Test
    public void testUpdateUser() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User userToUpdate = userBuilder.id(1L).name("Name Updated").build();
        User userUpdated = userStorage.updateUser(userToUpdate);

        assertThat(userUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Name Updated");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            User userNotExist = userBuilder.id(-1L).build();
            userStorage.updateUser(userNotExist);
        });
        assertEquals("User with id -1 not found", ex.getMessage());

        ex = assertThrows(NotFoundException.class, () -> {
            User userNotExist = userBuilder.id(999L).build();
            userStorage.updateUser(userNotExist);
        });
        assertEquals("User with id 999 not found", ex.getMessage());
    }

    @Test
    public void shouldAddFilmToStorage() {
        Film film = filmBuilder.build();
        Film addedFilm = filmStorage.addFilm(film);
        assertThat(addedFilm).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void shouldFindFilmById() {
        Film film = filmBuilder.build();
        Film addedFilm = filmStorage.addFilm(film);
        Film foundFilm = filmStorage.findFilmById(addedFilm.getId());
        assertThat(foundFilm).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("mpa", mpaBuilder.name("G").build());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmStorage.findFilmById(-1L));
        assertEquals("Movie with id -1 not found", exception.getMessage());

        exception = assertThrows(NotFoundException.class, () -> filmStorage.findFilmById(999L));
        assertEquals("Movie with id 999 not found", exception.getMessage());
    }

    @Test
    public void testListFilms() {
        List<Film> films = filmStorage.getAllFilms();
        assertThat(films)
                .isNotNull()
                .isEqualTo(Collections.emptyList());

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        films = filmStorage.getAllFilms();
        assertNotNull(films);
        assertEquals(films.size(), 1);
        assertEquals(films.get(0).getId(), 1);
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        Film filmToUpdate = filmBuilder.id(1L).name("Film name Updated").build();
        Film filmUpdated = filmStorage.updateFilm(filmToUpdate);
        assertThat(filmUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Film name Updated");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> filmStorage.updateFilm(filmBuilder.id(-1L).build())
        );
        assertEquals("Movie with id -1 not found", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> filmStorage.updateFilm(filmBuilder.id(999L).build())
        );
        assertEquals("Movie with id 999 not found", ex.getMessage());
    }

    @Test
    public void shouldListTopFilms() {
        List<Film> topFilms = filmStorage.getPopularFilms(10);
        assertThat(topFilms)
                .isNotNull()
                .isEqualTo(Collections.emptyList());

        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        userStorage.addUser(userBuilder.build());

        topFilms = filmStorage.getPopularFilms(1);
        assertNotNull(topFilms);
        assertEquals(topFilms.size(), 1);
        assertEquals(topFilms.get(0).getId(), 1);

        filmStorage.addLike(2, 1);
        topFilms = filmStorage.getPopularFilms(2);
        assertNotNull(topFilms);
        assertEquals(topFilms.size(), 2);
        assertEquals(topFilms.get(0).getId(), 2);
    }

    @Test
    public void shouldAddGenreToFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);
        List<Genre> genres = genreDao.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 1);
        assertEquals(genres.get(0).getId(), 1);

        filmStorage.addGenreToFilm(1, 2);
        genres = genreDao.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 2);
        assertEquals(genres.get(0).getId(), 1);
    }

    @Test
    public void testDeleteGenreFromFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);
        filmStorage.addGenreToFilm(1, 2);

        filmStorage.deleteGenreFromFilm(1, 2);

        List<Genre> genres = genreDao.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 1);
        assertEquals(genres.get(0).getId(), 1);

        filmStorage.deleteGenreFromFilm(1, 1);

        genres = genreDao.getGenresByFilm(1);
        assertThat(genres)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldClearAllGenresFromFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);
        filmStorage.addGenreToFilm(1, 2);
        filmStorage.clearGenresFromFilm(1);
        List<Genre> genres = genreDao.getGenresByFilm(1);
        assertThat(genres).isNotNull()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldAddFriend() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);

        friendshipDao.addFriend(1, 2);
        List<Long> friends = friendshipDao.getFriendsByUser(1);
        assertNotNull(friends);
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), 2);
    }

    @Test
    public void shouldListFriendsByUser() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        List<Long> friends = friendshipDao.getFriendsByUser(user.getId());
        assertThat(friends)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
        friendshipDao.addFriend(user.getId(), friend.getId());
        friends = friendshipDao.getFriendsByUser(user.getId());
        assertNotNull(friends);
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), friend.getId());
    }

    @Test
    public void shouldUpdateFriend() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(1, 2);

        friendshipDao.updateFriend(2, 1, true);
        List<Long> friends = friendshipDao.getFriendsByUser(2);
        assertNotNull(friends);
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), 1);
    }

    @Test
    public void shouldDeleteFriendship() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        friendshipDao.deleteFriend(user.getId(), friend.getId());
        List<Long> friends = friendshipDao.getFriendsByUser(user.getId());
        assertThat(friends).isNotNull();
    }

    @Test
    public void shouldAddLike() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        User user = userBuilder.build();
        userStorage.addUser(user);

        filmStorage.addLike(1, 1);
        List<Long> likes = filmStorage.getLikesByFilm(1);
        assertNotNull(likes);
        assertEquals(likes.size(), 1);
        assertEquals(likes.get(0), 1);
    }

    @Test
    public void shouldReturnEmptyListWhenNoLikesForFilmExist() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Long> likes = filmStorage.getLikesByFilm(film.getId());
        assertThat(likes).isNotNull().isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldDeleteLikeAndGetUpdatedLikesList() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        User user = userBuilder.build();
        userStorage.addUser(user);
        userStorage.addUser(user);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);

        filmStorage.deleteLike(1, 2);
        List<Long> likes = filmStorage.getLikesByFilm(1);
        assertNotNull(likes);
        assertEquals(likes.size(), 1);
        assertEquals(likes.get(0), 1);

        filmStorage.deleteLike(1, 1);
        likes = filmStorage.getLikesByFilm(1);
        assertThat(likes)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldReturnMpasList() {
        List<Mpa> mpas = mpaDao.getMpaList();
        assertNotNull(mpas);
        assertEquals(mpas.size(), 5);
        assertThat(mpas.get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void shouldFindMpaById() {
        Mpa mpa = mpaDao.findMpaById(1);
        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G")
                .hasFieldOrPropertyWithValue("description", "The film has no age restrictions");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> mpaDao.findMpaById(-1)
        );
        assertEquals("MPA rating with id -1 not found", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> mpaDao.findMpaById(999)
        );
        assertEquals("MPA rating with id 999 not found", ex.getMessage());
    }

    @Test
    public void shouldGetEmptyLikesListWhenNoLikesForFilmExist() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);

        List<Long> likes = filmStorage.getLikesByFilm(film.getId());

        assertThat(likes).isNotNull().isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldReturnGenresList() {
        List<Genre> genres = genreDao.getGenres();
        assertNotNull(genres);
        assertEquals(genres.size(), 6);
        assertThat(genres.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void shouldReturnEmptyGenresListIfNoGenresForFilmExist() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Genre> genres = genreDao.getGenresByFilm(film.getId());
        assertThat(genres).isNotNull().isEqualTo(Collections.emptyList());
    }
}