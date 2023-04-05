package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    List<String> getUserMails();

    List<User> findAllUsers();

    void create(@NotNull @Valid @RequestBody User user);

    //User update(@NotNull @Valid @RequestBody User user);
}
