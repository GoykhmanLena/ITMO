package ru.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.labs.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
