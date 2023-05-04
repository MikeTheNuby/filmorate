package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private long id;
    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Invalid e-mail entered.")
    private String email;
    @NotBlank(message = "Login cannot be empty.")
    @Pattern(regexp = "\\S+", message = "Login contains spaces.")
    private String login;
    private String name;
    @NotNull(message = "Date of birth cannot be empty.")
    @PastOrPresent(message = "Date of birth cannot be from the future.")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
