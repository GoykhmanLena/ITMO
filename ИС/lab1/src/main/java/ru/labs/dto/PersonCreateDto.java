package ru.labs.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.labs.model.Color;

@Getter
@Setter
@NoArgsConstructor
public class PersonCreateDto {

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @NotNull(message = "Цвет глаз обязателен")
    private Color eyeColor;

    private Color hairColor;

    private LocalDateTime birthday;

    @NotNull(message = "Вес обязателен")
    @Positive(message = "Вес должен быть больше 0")
    private Integer weight;

    @NotNull(message = "X координаты обязателен")
    private Long locationX;

    private Float locationY;

    private String locationName;
}
