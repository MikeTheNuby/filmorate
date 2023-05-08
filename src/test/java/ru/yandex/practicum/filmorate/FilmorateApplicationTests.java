package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
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
                .email("user@google.com")
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
                "users", "films", "friendship", "film_genre", "likes"
        );
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void testAddUser() {
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
                .isEmpty();
        User user = userBuilder.build();
        userStorage.addUser(user);
        users = userStorage.getAllUsers();
        assertThat(users)
                .isNotNull()
                .hasSize(1)
                .contains(user);
    }

    @Test
    public void shouldUpdateUser() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User updatedUser = userBuilder.id(1L).name("Name Updated").build();
        User result = userStorage.updateUser(updatedUser);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Name Updated");
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingNonExistingUser() {
        User userNotExist = userBuilder.id(-1L).build();
        User finalUserNotExist = userNotExist;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userStorage.updateUser(finalUserNotExist));
        assertEquals("User with id -1 not found", exception.getMessage());

        userNotExist = userBuilder.id(999L).build();
        User finalUserNotExist1 = userNotExist;
        exception = assertThrows(NotFoundException.class, () -> userStorage.updateUser(finalUserNotExist1));
        assertEquals("User with id 999 not found", exception.getMessage());
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
        Film foundFilm = filmStorage.getFilmById(addedFilm.getId());
        assertThat(foundFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(Film::getMpa)
                .isEqualTo(mpaBuilder.name("G").build());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(-1L));
        assertEquals("Movie with id -1 not found.", exception.getMessage());
        exception = assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(999L));
        assertEquals("Movie with id 999 not found.", exception.getMessage());
    }

    @Test
    public void shouldListFilms() {
        List<Film> films = filmStorage.getAllFilms();
        assertThat(films)
                .isNotNull()
                .isEqualTo(Collections.emptyList());

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
log.info("!!! " + filmStorage.getAllFilms().size());
        films = filmStorage.getAllFilms();
        assertThat(films)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        Film updatedFilm = filmBuilder.id(1L).name("Film Name Updated").build();
        Film result = filmStorage.updateFilm(updatedFilm);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Film Name Updated");
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            Film filmNotExist = filmBuilder.id(-1L).build();
            filmStorage.updateFilm(filmNotExist);
        });
        assertEquals("Movie with id -1 not found.", ex.getMessage());

        ex = assertThrows(NotFoundException.class, () -> {
            Film filmNotExist = filmBuilder.id(999L).build();
            filmStorage.updateFilm(filmNotExist);
        });
        assertEquals("Movie with id 999 not found.", ex.getMessage());
    }

    @Test
    public void shouldListTopFilms() {
        List<Film> topFilms = filmStorage.getPopularFilms(10);
        assertThat(topFilms)
                .isNotNull()
                .isEqualTo(Collections.emptyList());
        Film film1 = filmBuilder.name("Film 1").mpa(mpaBuilder.name("G").build()).build();
        Film film2 = filmBuilder.name("Film 2").mpa(mpaBuilder.name("G").build()).build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        User user = userBuilder.build();
        userStorage.addUser(user);
        filmStorage.addLike(2, 1);

        topFilms = filmStorage.getPopularFilms(2);
        assertThat(topFilms)
                .isNotNull()
                .hasSize(2)
                .containsExactly(film2, film1);
    }

    @Test
    public void shouldAddGenreToFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);
        List<Genre> genres = genreDao.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 1);
        assertThat(genres.get(0)).hasFieldOrPropertyWithValue("id", 1);
        filmStorage.addGenreToFilm(1, 2);
        genres = genreDao.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 2);
        assertThat(genres.get(0)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void shouldDeleteGenreFromFilm() {
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
        assertThat(genres)
                .isNotNull()
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
    public void shouldAddFriendForUser() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);

        friendshipDao.addFriend(user.getId(), friend.getId());
        List<Long> friends = friendshipDao.getFriendsByUser(user.getId());
        assertThat(friends)
                .isNotNull()
                .hasSize(1)
                .containsExactly(friend.getId());
    }

    @Test
    public void shouldUpdateFriendship() {
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
    public void testDeletingFriendship() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        friendshipDao.deleteFriend(user.getId(), friend.getId());
        List<Long> friendsList = friendshipDao.getFriendsByUser(user.getId());
        assertThat(friendsList).isNotNull();
    }

    @Test
    public void shouldAddLike() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        User user = userBuilder.build();
        userStorage.addUser(user);
        filmStorage.addLike(film.getId(), user.getId());
        List<Long> likesList = filmStorage.getLikesByFilm(film.getId());
        assertThat(likesList).isNotNull();
        assertThat(likesList.size()).isEqualTo(1);
        assertThat(likesList.get(0)).isEqualTo(user.getId());
    }

    @Test
    public void shouldReturnEmptyListWhenNoLikesForFilmExist() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Long> likesList = filmStorage.getLikesByFilm(film.getId());
        assertThat(likesList).isNotNull().isEqualTo(Collections.emptyList());
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
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        friendshipDao.deleteFriend(user.getId(), friend.getId());
        List<Long> friendsList = friendshipDao.getFriendsByUser(user.getId());
        assertThat(friendsList).isNotNull();
    }

    @Test
    public void testFindingMpaById() {
        Mpa mpa = mpaDao.findMpaById(1);
        assertThat(mpa).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G")
                .hasFieldOrPropertyWithValue("description", "The film has no age restrictions");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> mpaDao.findMpaById(-1));
        assertEquals("MPA rating with id -1 not found", ex.getMessage());

        ex = assertThrows(NotFoundException.class, () -> mpaDao.findMpaById(999));
        assertEquals("MPA rating with id 999 not found", ex.getMessage());
    }

    @Test
    public void shouldGetEmptyLikesListWhenNoLikesForFilmExist() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(user.getId(), friend.getId());
        friendshipDao.deleteFriend(user.getId(), friend.getId());
        List<Long> friendsList = friendshipDao.getFriendsByUser(user.getId());
        assertThat(friendsList).isNotNull();
    }

    @Test
    public void testGettingEmptyLikesListWhenNoLikesForFilmExist() {
        Mpa mpa = mpaDao.findMpaById(1);

        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G")
                .hasFieldOrPropertyWithValue("description", "The film has no age restrictions");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> mpaDao.findMpaById(-1));
        assertEquals("MPA rating with id -1 not found", ex.getMessage());

        ex = assertThrows(NotFoundException.class, () -> mpaDao.findMpaById(999));
        assertEquals("MPA rating with id 999 not found", ex.getMessage());
    }

    @Test
    public void shouldReturnGenresList() {
        List<Genre> genres = genreDao.getGenres();
        assertNotNull(genres);
        assertEquals(6, genres.size());
        assertThat(genres.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void shouldReturnEmptyGenresListIfNoGenresForFilmExist() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Genre> genres = genreDao.getGenresByFilm(film.getId());
        assertThat(genres).isNotNull().isEmpty();
    }
}