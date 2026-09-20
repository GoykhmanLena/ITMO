package ru.labs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.labs.model.MovieGenre;
import ru.labs.model.MpaaRating;

@Getter
@Setter
@NoArgsConstructor
public class MovieCreateDto {

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @NotNull(message = "X координаты обязателен")
    private Long x;

    @NotNull(message = "Y координаты обязателен")
    private Float y;

    @Positive(message = "Количество Oscar должно быть больше 0")
    private Long oscarsCount;

    @NotNull(message = "Budget обязателен")
    @Positive(message = "Budget должен быть больше 0")
    private Float budget;

    @NotNull(message = "Total box office обязателен")
    @Positive(message = "Total box office должен быть больше 0")
    private Long totalBoxOffice;

    private MpaaRating mpaaRating;

    @NotNull(message = "Оператор обязателен")
    private Long operatorId;

    private Long directorId;

    private Long screenwriterId;

    @NotNull(message = "Length обязателен")
    @Positive(message = "Length должен быть больше 0")
    private Long length;

    @NotNull(message = "Golden palm count обязателен")
    @Positive(message = "Golden palm count должен быть больше 0")
    private Integer goldenPalmCount;

    @NotNull(message = "USA box office обязателен")
    @Positive(message = "USA box office должен быть больше 0")
    private Float usaBoxOffice;

    @NotBlank(message = "Tagline не должен быть пустым")
    private String tagline;

    @NotNull(message = "Жанр обязателен")
    private MovieGenre genre;
}
