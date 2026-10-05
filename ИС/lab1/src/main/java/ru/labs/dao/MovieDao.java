package ru.labs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.labs.model.Movie;
import ru.labs.model.MovieGenre;

@Repository
public class MovieDao {

    @PersistenceContext
    private EntityManager em;

    public Movie findById(Long id) {
        return em.find(Movie.class, id);
    }

    public List<Movie> findAll(int page, int size, String nameFilter, String sort) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> root = cq.from(Movie.class);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтрация
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("name"), nameFilter));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        // Динамическая сортировка
        if (sort != null && !sort.trim().isEmpty()) {
            cq.orderBy(cb.asc(root.get(sort)));
        } else {
            cq.orderBy(cb.asc(root.get("id"))); // Сортировка по умолчанию
        }

        return em.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(m) FROM Movie m", Long.class)
                .getSingleResult();
    }

    public void save(Movie movie) {
        em.persist(movie);
    }

    public Movie update(Movie movie) {
        return em.merge(movie);
    }

    public void delete(Movie movie) {
        em.remove(em.contains(movie) ? movie : em.merge(movie));
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findByNameStartingWith(String prefix) {
        return em.createNativeQuery("SELECT * FROM get_movies_by_name_prefix(?1)", Movie.class)
                .setParameter(1, prefix)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findByGenreLessThan(MovieGenre genre) {
        return em.createNativeQuery("SELECT * FROM get_movies_by_genre_less_than(?1)", Movie.class)
                .setParameter(1, genre.name())
                .getResultList();
    }

    public void deleteByGenre(MovieGenre genre) {
        em.createNativeQuery("SELECT delete_movies_by_genre(?1)")
                .setParameter(1, genre.name())
                .getSingleResult();
    }

    public void redistributeOscars(MovieGenre fromGenre, MovieGenre toGenre) {
        em.createNativeQuery("SELECT redistribute_oscars(?1, ?2)")
                .setParameter(1, fromGenre.name())
                .setParameter(2, toGenre.name())
                .getSingleResult();

        em.getEntityManagerFactory().getCache().evict(Movie.class);
        em.clear();
    }
}
