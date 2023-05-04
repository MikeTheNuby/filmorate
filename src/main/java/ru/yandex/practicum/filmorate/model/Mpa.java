package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mpa {
    private int id;
    @Size(min = 1, max = 5, message = "The length of the rating name must be from 1 to 5 characters.")
    private String name;
    @Size(min = 1, max = 100, message = "Description length must be between 1 and 100 characters.")
    private String description;
}
