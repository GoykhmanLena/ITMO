package ru.labs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.MovieDao;
import ru.labs.model.Movie;
import ru.labs.model.MovieGenre;

@Service
@Transactional
public class MovieService {

    private final MovieDao movieDao;

    @Autowired
    public MovieService(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    @Transactional(readOnly = true)
    public Movie getById(Long id) {
        return movieDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Movie> getPage(int page, int size, String nameFilter, String sort) {
        return movieDao.findAll(page, size, nameFilter, sort);
    }

    @Transactional(readOnly = true)
    public long count() {
        return movieDao.count();
    }

    public Movie create(Movie movie) {
        movieDao.save(movie);
        return movie;
    }

    public Movie update(Movie movie) {
        return movieDao.update(movie);
    }

    public void delete(Long id) {
        Movie movie = movieDao.findById(id);
        if (movie != null) {
            movieDao.delete(movie);
        }
    }

    @Transactional(readOnly = true)
    public List<Movie> findByNameStartingWith(String prefix) {
        return movieDao.findByNameStartingWith(prefix);
    }

    @Transactional(readOnly = true)
    public List<Movie> findByGenreLessThan(MovieGenre genre) {
        return movieDao.findByGenreLessThan(genre);
    }

    public void deleteByGenre(MovieGenre genre) {
        movieDao.deleteByGenre(genre);
    }

    public void redistributeOscars(MovieGenre fromGenre, MovieGenre toGenre) {
        movieDao.redistributeOscars(fromGenre, toGenre);
    }
}
