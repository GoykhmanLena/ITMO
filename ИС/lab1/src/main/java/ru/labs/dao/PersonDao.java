package ru.labs.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.labs.model.Person;

@Repository
public class PersonDao {

    @PersistenceContext
    private EntityManager em;

    public Person findById(Long id) {
        return em.find(Person.class, id);
    }

    public List<Person> findAll() {
        return em.createQuery("SELECT p FROM Person p ORDER BY p.id", Person.class)
                .getResultList();
    }

    public void save(Person person) {
        em.persist(person);
    }

    public Person update(Person person) {
        return em.merge(person);
    }

    public void delete(Person person) {
        em.remove(em.contains(person) ? person : em.merge(person));
    }

    @SuppressWarnings("unchecked")
    public List<Person> findDirectorsWithoutOscars() {
        return em.createNativeQuery("SELECT * FROM get_directors_without_oscars()", Person.class)
                .getResultList();
    }
}
