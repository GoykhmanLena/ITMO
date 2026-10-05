package ru.labs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import ru.labs.model.Coordinates;
import ru.labs.model.MovieGenre;
import ru.labs.model.MpaaRating;

@Getter
@Setter
public class MovieDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Tagline не может быть пустым")
    private String tagline;

    @NotNull
    private MovieGenre genre;

    @NotNull
    @Positive
    private Float budget;

    @NotNull
    @Min(1)
    private Long totalBoxOffice;

    @NotNull
    @Positive
    private Float usaBoxOffice;

    @Min(1)
    private Long oscarsCount;

    @NotNull
    @Min(1)
    private Integer goldenPalmCount;

    @NotNull
    @Min(1)
    private Long length;

    private MpaaRating mpaaRating;

    @NotNull
    private Coordinates coordinates;

    private PersonDto operator;
    private PersonDto director;
    private PersonDto screenwriter;
}
