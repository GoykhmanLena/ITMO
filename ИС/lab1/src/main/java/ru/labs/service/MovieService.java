package ru.labs.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.model.Movie;
import ru.labs.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Movie create(Movie movie) {
        return movieRepository.save(movie);
    }

    @Transactional(readOnly = true)
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Movie with id " + id + " not found"));
    }

    @Transactional
    public void delete(Long id) {
        movieRepository.deleteById(id);
    }

    @Transactional
    public Movie update(Long id, Movie movie) {
        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Movie with id " + id + " not found"));

        existing.setName(movie.getName());
        existing.setCoordinates(movie.getCoordinates());
        existing.setOscarsCount(movie.getOscarsCount());
        existing.setBudget(movie.getBudget());
        existing.setTotalBoxOffice(movie.getTotalBoxOffice());
        existing.setMpaaRating(movie.getMpaaRating());
        existing.setDirector(movie.getDirector());
        existing.setScreenwriter(movie.getScreenwriter());
        existing.setOperator(movie.getOperator());
        existing.setLength(movie.getLength());
        existing.setGoldenPalmCount(movie.getGoldenPalmCount());
        existing.setUsaBoxOffice(movie.getUsaBoxOffice());
        existing.setTagline(movie.getTagline());
        existing.setGenre(movie.getGenre());

        return existing;
    }

    @Transactional(readOnly = true)
    public Page<Movie> findPage(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }
}
