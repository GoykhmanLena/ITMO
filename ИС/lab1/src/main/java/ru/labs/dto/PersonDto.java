package ru.labs.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import ru.labs.model.Color;
import ru.labs.model.Location;

@Getter
@Setter
public class PersonDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Цвет глаз обязателен")
    private Color eyeColor;

    private Color hairColor;

    @NotNull
    @Positive(message = "Вес должен быть больше 0")
    private Integer weight;

    @NotNull
    @Valid
    private Location location;
}
