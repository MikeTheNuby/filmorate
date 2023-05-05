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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("DbUserService")
    private final UserService service;

    User user;
    String url = "/users";

    User.UserBuilder userBuilder;

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    void setupBuilder() {
        userBuilder = User.builder()
                .email("e@mail.ru")
                .login("Login")
                .birthday(LocalDate.of(1985, 9, 7));
    }

    @Test
    void shouldCreateMockMvc() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void shouldReturnEmptyUsersList() throws Exception {
        when(service.getAllUsers()).thenReturn(Collections.emptyList());
        this.mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnSingleUserList() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(userBuilder.id(1).login("User 1 Login")
                .email("user_1@google.com").name("User 1 Name").build()));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void shouldReturnListWithTwoUsers() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(
                userBuilder.id(1).login("User 1 Login").email("user_1@google.com").name("User 1 Name").build(),
                userBuilder.id(2).login("User 2 Login").email("user_2@google.com").name("User 2 Name").build()
        ));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("User 1 Login", "User 2 Login")));
    }

    @Test
    void shouldAddRegularUser() throws Exception {
        User user = userBuilder.build();
        User userAdded = userBuilder.id(1).name("Login").build();
        String json = objectMapper.writeValueAsString(user);
        String jsonAdded = objectMapper.writeValueAsString(userAdded);
        when(service.addUser(user)).thenReturn(userAdded);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void shouldAddNewbornUser() throws Exception {
        User user = userBuilder.birthday(LocalDate.now()).build();
        User userAdded = userBuilder.id(1).name("Login").birthday(LocalDate.now()).build();
        String json = objectMapper.writeValueAsString(user);
        String jsonAdded = objectMapper.writeValueAsString(userAdded);
        when(service.addUser(user)).thenReturn(userAdded);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void shouldThrowExceptionOnInvalidLogin() throws Exception {
        user = userBuilder.login(" login").build();
        String json = objectMapper.writeValueAsString(user);

        when(service.addUser(user)).thenReturn(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Login contains spaces."));

        user = userBuilder.login("").build();
        json = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Login cannot be empty."));
    }

    @Test
    void shouldThrowExceptionOnInvalidEmail() throws Exception {
        user = userBuilder.email("").build();
        String json = objectMapper.writeValueAsString(user);

        when(service.addUser(user)).thenReturn(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Email cannot be empty."));

        user = userBuilder.email("email@").build();
        json = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Invalid e-mail entered."));
    }

    @Test
    void shouldThrowExceptionOnInvalidBirthday() throws Exception {
        user = userBuilder.birthday(null).build();
        String json = objectMapper.writeValueAsString(user);
        System.out.println(json);

        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Date of birth cannot be empty."));

        user = userBuilder.birthday(LocalDate.now().plusDays(1)).build();
        json = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Date of birth cannot be from the future."));
    }

    @Test
    void shouldUpdateExistingUser() throws Exception {
        User userToUpdate = userBuilder.id(1).name("Name").build();
        String jsonToUpdate = objectMapper.writeValueAsString(userToUpdate);
        when(service.updateUser(userToUpdate)).thenReturn(userToUpdate);
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToUpdate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonToUpdate));
    }

    @Test
    void updateUserNotExistingId() throws Exception {
        user = userBuilder.id(0).name("Name").build();
        String json = objectMapper.writeValueAsString(user);

        when(service.updateUser(user)).thenThrow(new NotFoundException("Missing id."));
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage().equals("Missing id."));

        user = userBuilder.id(1).build();
        json = objectMapper.writeValueAsString(user);

        when(service.updateUser(user)).thenThrow(new NotFoundException("User with id 1 not found."));
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("User with id 1 not found."));
    }

    @Test
    void shouldReturnEmptyFriendsListForGivenUserId() throws Exception {
        when(service.getFriends(1)).thenReturn(Collections.emptyList());
        this.mockMvc
                .perform(get(url + "/1/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnSingleFriendsListForGivenUserId() throws Exception {
        when(service.getFriends(1)).thenReturn(List.of(
                userBuilder.id(2).login("User 2 Login").email("user_2@google.com").name("User 2 Name").build()));
        mockMvc.perform(get(url + "/1/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void shouldThrowNotFoundExceptionOnListFriendsForNonExistentId() throws Exception {
        when(service.getFriends(1)).thenThrow(new NotFoundException("User with id 1 not found."));
        mockMvc.perform(get(url + "/1/friends"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> Objects.requireNonNull(mvcResult.getResolvedException())
                        .getMessage().equals("User with id 1 not found."));
    }

    @Test
    void testListFriendsWithInvalidId() throws Exception {
        this.mockMvc
                .perform(get(url + "/abc/friends"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                                .equals("Variable id: abc must be long type."));
    }

    @Test
    void testCommonFriendsShouldReturnEmptyList() throws Exception {
        when(service.getCommonFriends(1, 2)).thenReturn(Collections.emptyList());
        this.mockMvc.perform(get(url + "/1/friends/common/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testListSingleCommonFriend() throws Exception {
        when(service.getCommonFriends(1, 2)).thenReturn(List.of(userBuilder.id(3)
                .login("Login3").email("user_3@google.com").name("User 3 Name").build()));
        mockMvc.perform(get(url + "/1/friends/common/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(3)));
    }

    @Test
    void testListCommonFriendsNullId() throws Exception {
        this.mockMvc.perform(get(url + "/friends/common/"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult ->
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage().equals("Unknown Request."));
    }

    @Test
    void testFindUserById() throws Exception {
        user = userBuilder.id(1).name("Login").build();
        String json = objectMapper.writeValueAsString(user);
        when(service.findUserById(1)).thenReturn(user);
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void testFindUserByNotExistingId() throws Exception {
        when(service.findUserById(1)).thenThrow(new NotFoundException("User with id 1 not found."));
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> Objects.requireNonNull(mvcResult.getResolvedException())
                        .getMessage().equals("User with id 1 not found."));
    }

    @Test
    void testAddFriend() throws Exception {
        when(service.addFriend(1, 2)).thenReturn(List.of(2L));
        mockMvc.perform(put(url + "/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0]", is(2)));
    }

    @Test
    void testDeleteFriend() throws Exception {
        List<Long> expected = Collections.emptyList();
        when(service.deleteFriend(1, 2)).thenReturn(expected);
        mockMvc.perform(delete(url + "/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
}