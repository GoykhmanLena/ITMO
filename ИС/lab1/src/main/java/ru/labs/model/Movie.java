package ru.labs.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Строка не может быть пустой")
    @Column(nullable = false)
    private String name;

    @Valid
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Positive
    @Column(name = "oscars_count", nullable = true)
    private Long oscarsCount;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Float budget;

    @Positive
    @Column(name = "total_box_office", nullable = false)
    private long totalBoxOffice;

    @Enumerated(EnumType.STRING)
    @Column(name = "mpaa_rating", nullable = true)
    private MpaaRating mpaaRating;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = true)
    private Person director;

    @ManyToOne
    @JoinColumn(name = "screenwriter_id", nullable = true)
    private Person screenwriter;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = false)
    private Person operator;

    @Positive
    @Column(nullable = false)
    private long length;

    @Positive
    @Column(name = "golden_palm_count", nullable = false)
    private int goldenPalmCount;

    @Positive
    @Column(name = "usa_box_office", nullable = false)
    private float usaBoxOffice;

    @NotBlank(message = "Строка не может быть пустой")
    @Column(nullable = false)
    private String tagline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieGenre genre;

    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }
}
