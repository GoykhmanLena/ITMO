package ru.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.labs.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
