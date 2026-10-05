package ru.labs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import ru.labs.model.Color;
import ru.labs.model.Movie;
import ru.labs.model.MovieGenre;
import ru.labs.service.MovieService;
import ru.labs.service.PersonService;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private static final int PAGE_SIZE = 10;

    private final MovieService movieService;
    private final PersonService personService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MovieController(MovieService movieService, PersonService personService,
            SimpMessagingTemplate messagingTemplate) {
        this.movieService = movieService;
        this.personService = personService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) String sort,
            Model model) {
        List<Movie> movies = movieService.getPage(page, PAGE_SIZE, nameFilter, sort);
        long total = movieService.count();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        model.addAttribute("movies", movies);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", total);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("sort", sort);
        return "movies/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Movie movie = movieService.getById(id);
        if (movie == null)
            return "redirect:/movies";
        model.addAttribute("movie", movie);
        return "movies/view";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", MovieGenre.values());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("colors", Color.values());
        return "movies/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", MovieGenre.values());
            model.addAttribute("persons", personService.getAll());
            return "movies/form";
        }

        if (movie.getOperator() != null) {
            if (movie.getOperator().getId() != null) {
                movie.setOperator(personService.getById(movie.getOperator().getId()));
            } else {
                personService.create(movie.getOperator());
            }
        }

        if (movie.getDirector() != null) {
            if (movie.getDirector().getId() != null) {
                movie.setDirector(personService.getById(movie.getDirector().getId()));
            } else if (movie.getDirector().getName() != null && !movie.getDirector().getName().trim().isEmpty()) {
                personService.create(movie.getDirector());
            } else {
                movie.setDirector(null);
            }
        }

        if (movie.getScreenwriter() != null) {
            if (movie.getScreenwriter().getId() != null) {
                movie.setScreenwriter(personService.getById(movie.getScreenwriter().getId()));
            } else if (movie.getScreenwriter().getName() != null
                    && !movie.getScreenwriter().getName().trim().isEmpty()) {
                personService.create(movie.getScreenwriter());
            } else {
                movie.setScreenwriter(null);
            }
        }
        movieService.create(movie);

        messagingTemplate.convertAndSend("/topic/movies", "update");
        return "redirect:/movies";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getById(id);
        if (movie == null)
            return "redirect:/movies";
        model.addAttribute("movie", movie);
        model.addAttribute("genres", MovieGenre.values());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("colors", Color.values());
        return "movies/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute Movie movie, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", MovieGenre.values());
            model.addAttribute("persons", personService.getAll());

            messagingTemplate.convertAndSend("/topic/movies", "update");
            return "movies/form";
        }

        Movie existing = movieService.getById(id);
        if (existing == null)
            return "redirect:/movies";

        if (movie.getOperator() != null) {
            if (movie.getOperator().getId() != null) {
                movie.setOperator(personService.getById(movie.getOperator().getId()));
            } else {
                personService.create(movie.getOperator());
            }
        }

        if (movie.getDirector() != null) {
            if (movie.getDirector().getId() != null) {
                movie.setDirector(personService.getById(movie.getDirector().getId()));
            } else if (movie.getDirector().getName() != null && !movie.getDirector().getName().trim().isEmpty()) {
                personService.create(movie.getDirector());
            } else {
                movie.setDirector(null);
            }
        }

        if (movie.getScreenwriter() != null) {
            if (movie.getScreenwriter().getId() != null) {
                movie.setScreenwriter(personService.getById(movie.getScreenwriter().getId()));
            } else if (movie.getScreenwriter().getName() != null
                    && !movie.getScreenwriter().getName().trim().isEmpty()) {
                personService.create(movie.getScreenwriter());
            } else {
                movie.setScreenwriter(null);
            }
        }

        if (movie.getCoordinates() == null || movie.getCoordinates().getId() == null) {
            movie.setCoordinates(existing.getCoordinates());
        }

        movie.setId(id);
        movie.setCreationDate(existing.getCreationDate());
        movieService.update(movie);
        return "redirect:/movies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        movieService.delete(id);

        messagingTemplate.convertAndSend("/topic/movies", "update");
        return "redirect:/movies";
    }
}
