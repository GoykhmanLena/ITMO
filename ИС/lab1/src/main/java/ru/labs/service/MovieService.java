package ru.labs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.MovieDao;
import ru.labs.dto.MovieDto;
import ru.labs.model.Movie;
import ru.labs.model.MovieGenre;

@Service
@Transactional
public class MovieService {

    private final MovieDao movieDao;
    private final PersonService personService;

    @Autowired
    public MovieService(MovieDao movieDao, PersonService personService) {
        this.movieDao = movieDao;
        this.personService = personService;
    }

    @Transactional(readOnly = true)
    public MovieDto getById(Long id) {
        Movie movie = movieDao.findById(id);
        return convertToDto(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getPage(int page, int size, String nameFilter, String sort) {
        return movieDao.findAll(page, size, nameFilter, sort).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long count() {
        return movieDao.count();
    }

    @Transactional(readOnly = true)
    public long count(String nameFilter) {
        return movieDao.count(nameFilter);
    }

    public void delete(Long id) {
        Movie movie = movieDao.findById(id);
        if (movie != null) {
            movieDao.delete(movie);
        }
    }

    public void deleteByGenre(MovieGenre genre) {
        movieDao.deleteByGenre(genre);
    }

    @Transactional(readOnly = true)
    public List<MovieDto> findByNameStartingWith(String prefix) {
        return movieDao.findByNameStartingWith(prefix).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDto> findByGenreLessThan(MovieGenre genre) {
        return movieDao.findByGenreLessThan(genre).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void redistributeOscars(MovieGenre from, MovieGenre to) {
        movieDao.redistributeOscars(from, to);
    }

    public MovieDto create(MovieDto dto) {
        Movie movie = new Movie();
        copyEditableFields(dto, movie);
        movieDao.save(movie);
        return convertToDto(movie);
    }

    public MovieDto update(Long id, MovieDto dto) {
        Movie movie = movieDao.findById(id);
        if (movie == null) {
            return null;
        }
        copyEditableFields(dto, movie);
        movieDao.update(movie);
        return convertToDto(movie);
    }

    private void copyEditableFields(MovieDto source, Movie target) {
        target.setName(source.getName());
        target.setTagline(source.getTagline());
        target.setGenre(source.getGenre());
        target.setBudget(source.getBudget());
        target.setTotalBoxOffice(source.getTotalBoxOffice());
        target.setUsaBoxOffice(source.getUsaBoxOffice());
        target.setOscarsCount(source.getOscarsCount());
        target.setGoldenPalmCount(source.getGoldenPalmCount());
        target.setLength(source.getLength());
        target.setMpaaRating(source.getMpaaRating());
        target.setCoordinates(source.getCoordinates());

        target.setOperator(personService.resolveOrCreate(source.getOperator()));
        target.setDirector(personService.resolveOrCreate(source.getDirector()));
        target.setScreenwriter(personService.resolveOrCreate(source.getScreenwriter()));
    }

    public MovieDto convertToDto(Movie movie) {
        if (movie == null)
            return null;
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setTagline(movie.getTagline());
        dto.setGenre(movie.getGenre());
        dto.setBudget(movie.getBudget());
        dto.setTotalBoxOffice(movie.getTotalBoxOffice());
        dto.setUsaBoxOffice(movie.getUsaBoxOffice());
        dto.setOscarsCount(movie.getOscarsCount());
        dto.setGoldenPalmCount(movie.getGoldenPalmCount());
        dto.setLength(movie.getLength());
        dto.setMpaaRating(movie.getMpaaRating());
        dto.setCoordinates(movie.getCoordinates());
        dto.setCreationDate(movie.getCreationDate());

        if (movie.getOperator() != null) {
            dto.setOperator(personService.convertToDto(movie.getOperator()));
        }
        if (movie.getDirector() != null) {
            dto.setDirector(personService.convertToDto(movie.getDirector()));
        }
        if (movie.getScreenwriter() != null) {
            dto.setScreenwriter(personService.convertToDto(movie.getScreenwriter()));
        }
        return dto;
    }
}
