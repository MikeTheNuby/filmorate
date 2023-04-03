package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserStorage {

    List<User> findAllUsers();

    User create(@NotNull @Valid @RequestBody User user);

    User update(@NotNull @Valid @RequestBody User user);
}
