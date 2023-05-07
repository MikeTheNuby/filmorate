package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("DbFilmService")
    private final FilmService service;

    Film film;
    String url = "/films";
    private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);
    private static final int COUNT = 10;

    Film.FilmBuilder filmBuilder;

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    void setupBuilder() {
        int duration = 90;
        filmBuilder = Film.builder()
                .name("Film Title")
                .description("Film Description")
                .releaseDate(testReleaseDate)
                .duration(duration);
    }

    @Test
    void shouldCreateMockMvc() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void testGetAllFilmsReturnsEmptyList() throws Exception {
        when(service.getAllFilms()).thenReturn(Collections.emptyList());
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnSingleFilm() throws Exception {
        when(service.getAllFilms()).thenReturn(List.of(
                filmBuilder.id(1).build()));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void shouldReturnListOfTwoFilms() throws Exception {
        when(service.getAllFilms()).thenReturn(List.of(
                filmBuilder.id(1).name("Film_1 Title").build(),
                filmBuilder.id(2).name("Film_2 Title").build()
        ));

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Film_1 Title", "Film_2 Title")));
    }

    @Test
    void addRegularFilmTest() throws Exception {
        film = filmBuilder.build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = objectMapper.writeValueAsString(film);
        String jsonAdded = objectMapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void testAddFilm() throws Exception {
        film = filmBuilder
                .name("Le Jardinier")
                .description("1895 French short silent film. " +
                        "One of the first films made by the Lumiere brothers; the first staged film comedy.")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = objectMapper.writeValueAsString(film);
        String jsonAdded = objectMapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void testAddFilmFailRelease() throws Exception {
        film = filmBuilder
                .name("Phantasmagoria")
                .description("silent short cartoon by Emil Kohl. It is the world's first hand-drawn cartoon. " +
                        "The premiere took place in Paris on August 17, 1908.")
                .releaseDate(LocalDate.of(1888, 10, 14))
                .duration(1)
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Objects.requireNonNull(mvcResult.getResolvedException())
                        .getMessage()
                        .equals("Release date before December 28, 1895."));

        film = filmBuilder
                .releaseDate(null)
                .build();
        json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Objects.requireNonNull(mvcResult.getResolvedException())
                        .getMessage()
                        .equals("Missing release date."));
    }

    @Test
    void testAddFilmSuccess() throws Exception {
        film = filmBuilder
                .name("The Shawshank Redemption")
                .description("Chronicles the experiences of a formerly successful banker as a prisoner " +
                        "in the gloomy jailhouse of Shawshank after being found guilty of a crime he did not commit.")
                .releaseDate(LocalDate.of(1994, 9, 10))
                .duration(142)
                .build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = objectMapper.writeValueAsString(film);
        String jsonAdded = objectMapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void addFilmWithInvalidDescription() throws Exception {
        film = filmBuilder
                .name("The Shawshank Redemption")
                .description("Chronicles the experiences of a formerly successful banker as a prisoner " +
                        "in the gloomy jailhouse of Shawshank after being found guilty of a crime he did not commit. " +
                        "The film portrays the man's unique way of dealing with his new, torturous life; along the way " +
                        "he befriends a number of fellow prisoners, most notably a wise long-term inmate named Red.")
                .releaseDate(LocalDate.of(1994, 9, 10))
                .duration(142)
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Description length must be between 1 and 200 characters."));

        film = filmBuilder.description("").build();
        json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Description length must be between 1 and 200 characters."));

    }

    @Test
    public void addFilmFailName() throws Exception {
        film = filmBuilder
                .name("")
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage().equals("Title missing."));
    }

    @Test
    public void addFilmInvalidDuration() throws Exception {
        film = filmBuilder
                .name("The Shawshank Redemption")
                .description("Chronicles the experiences of a formerly successful banker as a prisoner " +
                        "in the gloomy jailhouse of Shawshank after being found guilty of a crime he did not commit.")
                .releaseDate(LocalDate.of(1994, 9, 10))
                .duration(-142)
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Movie duration cannot be negative."));
    }

    @Test
    public void addFilmDuration0() throws Exception {
        film = filmBuilder
                .duration(0)
                .build();
        Film filmAdded = filmBuilder.id(1).build();
        String json = objectMapper.writeValueAsString(film);
        String jsonAdded = objectMapper.writeValueAsString(filmAdded);

        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));

        Film filmWithoutDuration = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(testReleaseDate)
                .build();
        filmAdded = filmBuilder.id(1).duration(0).build();
        when(service.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void updateFilmExistingId() throws Exception {
        film = filmBuilder.id(1).build();
        String json = objectMapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenReturn(film);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void testUpdateFilmWithNonexistentId() throws Exception {
        film = filmBuilder.build();
        String json = objectMapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenThrow(new NotFoundException("Missing id."));
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Missing id."));

        film = filmBuilder.id(1).build();
        json = objectMapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenThrow(new NotFoundException("Movie with id 1 not found."));
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Movie with id 1 not found."));
    }

    @Test
    void testFindFilmById() throws Exception {
        film = filmBuilder.id(1).build();
        String json = objectMapper.writeValueAsString(film);

        when(service.getFilmById(1)).thenReturn(film);
        mockMvc.perform(get(url + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void testFindFilmByIdWithNonExistingId() throws Exception {
        when(service.getFilmById(1)).thenThrow(new NotFoundException("Movie with id 1 not found."));
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Objects.requireNonNull(result.getResolvedException())
                        .getMessage().equals("Movie with id 1 not found."));
    }

    @Test
    void shouldListEmptyPopularFilms() throws Exception {
        when(service.getPopularFilms(COUNT)).thenReturn(Collections.emptyList());
        this.mockMvc
                .perform(get(url + "/popular?count=" + COUNT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldListPopularFilms() throws Exception {
        Film film1 = filmBuilder.id(1).name("Film_1 Title").build();
        Film film2 = filmBuilder.id(2).name("Film_2 Title").build();
        when(service.getPopularFilms(2)).thenReturn(List.of(film1, film2));

        mockMvc.perform(get(url + "/popular?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void shouldAddLike() throws Exception {
        when(service.addLike(1, 1)).thenReturn(List.of(1L));
        mockMvc.perform(put(url + "/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0]", is(1)));
    }

    @Test
    void testDeleteLike() throws Exception {
        when(service.deleteLike(1, 1)).thenReturn(Collections.emptyList());
        mockMvc.perform(delete(url + "/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
}