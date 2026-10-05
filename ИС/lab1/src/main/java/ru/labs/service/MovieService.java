package ru.labs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.MovieDao;
import ru.labs.dto.MovieDto;
import ru.labs.dto.PersonDto;
import ru.labs.model.Movie;
import ru.labs.model.MovieGenre;
import ru.labs.model.Person;

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

    @Transactional
    public void saveFromDto(MovieDto dto) {
        Movie movie;
        if (dto.getId() != null) {
            movie = movieDao.findById(dto.getId());
        } else {
            movie = new Movie();
            movie.setCreationDate(java.time.LocalDateTime.now());
        }

        movie.setName(dto.getName());
        movie.setTagline(dto.getTagline());
        movie.setGenre(dto.getGenre());
        movie.setBudget(dto.getBudget());
        movie.setTotalBoxOffice(dto.getTotalBoxOffice());
        movie.setUsaBoxOffice(dto.getUsaBoxOffice());
        movie.setOscarsCount(dto.getOscarsCount());
        movie.setGoldenPalmCount(dto.getGoldenPalmCount());
        movie.setLength(dto.getLength());
        movie.setCoordinates(dto.getCoordinates());
        movie.setMpaaRating(dto.getMpaaRating());

        movie.setOperator(processPerson(dto.getOperator()));
        movie.setDirector(processPerson(dto.getDirector()));
        movie.setScreenwriter(processPerson(dto.getScreenwriter()));

        if (movie.getId() == null) {
            movieDao.save(movie);
        } else {
            movieDao.update(movie);
        }
    }

    private Person processPerson(PersonDto pDto) {
        if (pDto == null)
            return null;

        if (pDto.getId() != null) {
            return personService.getById(pDto.getId());
        }

        if (pDto.getName() != null && !pDto.getName().trim().isEmpty()) {
            Person person = new Person();
            person.setName(pDto.getName());
            person.setEyeColor(pDto.getEyeColor());
            person.setHairColor(pDto.getHairColor());
            person.setWeight(pDto.getWeight());
            person.setLocation(pDto.getLocation());

            personService.create(person);
            return person;
        }
        return null;
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
        dto.setCoordinates(movie.getCoordinates());
        dto.setMpaaRating(movie.getMpaaRating());

        dto.setOperator(personToDto(movie.getOperator()));
        dto.setDirector(personToDto(movie.getDirector()));
        dto.setScreenwriter(personToDto(movie.getScreenwriter()));
        return dto;
    }

    private PersonDto personToDto(Person person) {
        if (person == null)
            return null;
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setEyeColor(person.getEyeColor());
        dto.setHairColor(person.getHairColor());
        dto.setWeight(person.getWeight());
        dto.setLocation(person.getLocation());
        return dto;
    }
}
