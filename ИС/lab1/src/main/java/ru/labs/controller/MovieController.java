package ru.labs.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import ru.labs.dto.MovieCreateDto;
import ru.labs.model.Coordinates;
import ru.labs.model.Movie;
import ru.labs.model.Person;
import ru.labs.service.MovieService;
import ru.labs.service.PersonService;

@Controller
public class MovieController {

    private final MovieService movieService;
    private final PersonService personService;

    public MovieController(
            MovieService movieService,
            PersonService personService) {
        this.movieService = movieService;
        this.personService = personService;
    }

    @GetMapping("/movies")
    public String movies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Movie> moviePage = movieService.findPage(pageable);

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("totalItems", moviePage.getTotalElements());
        model.addAttribute("size", size);

        return "movies";
    }

    @GetMapping("/movies/new")
    public String newMovieForm(Model model) {
        model.addAttribute("movie", new MovieCreateDto());
        model.addAttribute("persons", personService.findAll());

        return "movie-form";
    }

    @GetMapping("/movies/{id}")
    public String viewMovie(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("movie", movieService.findById(id));

        return "movie-view";
    }

    @GetMapping("/movies/{id}/edit")
    public String editMovieForm(
            @PathVariable Long id,
            Model model) {

        Movie movie = movieService.findById(id);

        MovieCreateDto dto = new MovieCreateDto();

        dto.setName(movie.getName());
        dto.setX(movie.getCoordinates().getX());
        dto.setY(movie.getCoordinates().getY());
        dto.setOscarsCount(movie.getOscarsCount());
        dto.setBudget(movie.getBudget());
        dto.setTotalBoxOffice(movie.getTotalBoxOffice());
        dto.setMpaaRating(movie.getMpaaRating());

        if (movie.getOperator() != null) {
            dto.setOperatorId(movie.getOperator().getId());
        }

        if (movie.getDirector() != null) {
            dto.setDirectorId(movie.getDirector().getId());
        }

        if (movie.getScreenwriter() != null) {
            dto.setScreenwriterId(movie.getScreenwriter().getId());
        }

        dto.setLength(movie.getLength());
        dto.setGoldenPalmCount(movie.getGoldenPalmCount());
        dto.setUsaBoxOffice(movie.getUsaBoxOffice());
        dto.setTagline(movie.getTagline());
        dto.setGenre(movie.getGenre());

        model.addAttribute("movie", dto);
        model.addAttribute("movieId", id);
        model.addAttribute("persons", personService.findAll());

        return "movie-edit";
    }

    @PostMapping("/movies")
    public String createMovie(
            @Valid @ModelAttribute("movie") MovieCreateDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("persons", personService.findAll());
            return "movie-form";
        }

        Person operator = personService.findById(dto.getOperatorId());

        Person director = null;
        if (dto.getDirectorId() != null) {
            director = personService.findById(dto.getDirectorId());
        }

        Person screenwriter = null;
        if (dto.getScreenwriterId() != null) {
            screenwriter = personService.findById(dto.getScreenwriterId());
        }

        Movie movie = new Movie();

        movie.setName(dto.getName());

        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());

        movie.setCoordinates(coordinates);

        movie.setOscarsCount(dto.getOscarsCount());
        movie.setBudget(dto.getBudget());
        movie.setTotalBoxOffice(dto.getTotalBoxOffice());
        movie.setMpaaRating(dto.getMpaaRating());

        movie.setDirector(director);
        movie.setScreenwriter(screenwriter);
        movie.setOperator(operator);

        movie.setLength(dto.getLength());
        movie.setGoldenPalmCount(dto.getGoldenPalmCount());
        movie.setUsaBoxOffice(dto.getUsaBoxOffice());
        movie.setTagline(dto.getTagline());
        movie.setGenre(dto.getGenre());

        movieService.create(movie);

        return "redirect:/movies";
    }

    @PostMapping("/movies/{id}/edit")
    public String updateMovie(
            @PathVariable Long id,
            @Valid @ModelAttribute("movie") MovieCreateDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("movieId", id);
            model.addAttribute("persons", personService.findAll());
            return "movie-edit";
        }

        Person operator = personService.findById(dto.getOperatorId());

        Person director = null;
        if (dto.getDirectorId() != null) {
            director = personService.findById(dto.getDirectorId());
        }

        Person screenwriter = null;
        if (dto.getScreenwriterId() != null) {
            screenwriter = personService.findById(dto.getScreenwriterId());
        }

        Movie movie = new Movie();

        movie.setName(dto.getName());

        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());
        movie.setCoordinates(coordinates);

        movie.setOscarsCount(dto.getOscarsCount());
        movie.setBudget(dto.getBudget());
        movie.setTotalBoxOffice(dto.getTotalBoxOffice());
        movie.setMpaaRating(dto.getMpaaRating());
        movie.setDirector(director);
        movie.setScreenwriter(screenwriter);
        movie.setOperator(operator);
        movie.setLength(dto.getLength());
        movie.setGoldenPalmCount(dto.getGoldenPalmCount());
        movie.setUsaBoxOffice(dto.getUsaBoxOffice());
        movie.setTagline(dto.getTagline());
        movie.setGenre(dto.getGenre());

        movieService.update(id, movie);

        return "redirect:/movies/" + id;
    }

    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable Long id) {

        movieService.delete(id);

        return "redirect:/movies";
    }
}
