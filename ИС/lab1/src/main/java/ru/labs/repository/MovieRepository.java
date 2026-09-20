package ru.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.labs.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
