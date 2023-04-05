package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
@Slf4j
public class Validator {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    public void userValidate(User user) {
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            log.error("User email is empty. {}", user.getLogin());
            throw new ValidationException("User email is empty.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()
                || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("User login is empty. {}", user.getName());
            throw new ValidationException("User login is empty.");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name is empty. {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday().atStartOfDay(ZoneOffset.of("+03:00")).toInstant().toEpochMilli()
                >= System.currentTimeMillis()) {
            log.error("User WrongDate. {}", user.getName());
            throw new ValidationException("User WrongDate.");
        }
    }

    public void filmValidate(Film film) {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.error("Film title is empty. {}", film.getId());
            throw new ValidationException("Film title is empty.");
        }

        if (film.getDescription().length() > 200) {
            log.error("Film description to long. {}", film.getDescription());
            throw new ValidationException("Film description to long.");
        }

        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            log.error("Film release date before the earliest. {}", film.getReleaseDate());
            throw new ValidationException("Film release date before the earliest.");
        }

        if (film.getDuration() <= 0) {
            log.error("Film release duration is negative value. {}", film.getDuration());
            throw new ValidationException("Film release duration is negative value.");
        }
    }


}