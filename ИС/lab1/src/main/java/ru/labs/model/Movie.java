package ru.labs.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must not be empty")
    @Column(nullable = false)
    private String name;

    @Valid
    @Embedded
    @NotNull(message = "Coordinates must not be null")
    private Coordinates coordinates;

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Positive(message = "Oscars count must be greater than 0")
    @Column(name = "oscars_count")
    private Long oscarsCount;

    @NotNull
    @Positive(message = "Budget must be greater than 0")
    @Column(nullable = false)
    private Float budget;

    @Positive(message = "Total box office must be greater than 0")
    @Column(name = "total_box_office", nullable = false)
    private long totalBoxOffice;

    @Enumerated(EnumType.STRING)
    @Column(name = "mpaa_rating")
    private MpaaRating mpaaRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id")
    private Person director;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screenwriter_id")
    private Person screenwriter;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operator_id", nullable = false)
    private Person operator;

    @Positive(message = "Length must be greater than 0")
    @Column(nullable = false)
    private long length;

    @Positive(message = "Golden palm count must be greater than 0")
    @Column(name = "golden_palm_count", nullable = false)
    private int goldenPalmCount;

    @Positive(message = "USA box office must be greater than 0")
    @Column(name = "usa_box_office", nullable = false)
    private float usaBoxOffice;

    @NotNull
    @Column(nullable = false)
    private String tagline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieGenre genre;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }
}
