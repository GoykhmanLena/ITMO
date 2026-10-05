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
import ru.labs.dto.MovieDto;
import ru.labs.model.Color;
import ru.labs.model.Movie;
import ru.labs.model.MovieGenre;
import ru.labs.model.MpaaRating;
import ru.labs.service.MovieService;
import ru.labs.service.PersonService;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private static final int PAGE_SIZE = 4;

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
        model.addAttribute("movie", new MovieDto());
        model.addAttribute("genres", MovieGenre.values());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("colors", Color.values());
        model.addAttribute("mpaaRatings", MpaaRating.values());
        return "movies/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getById(id);
        if (movie == null)
            return "redirect:/movies";

        model.addAttribute("movie", movieService.convertToDto(movie));
        model.addAttribute("genres", MovieGenre.values());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("colors", Color.values());
        model.addAttribute("mpaaRatings", MpaaRating.values());
        return "movies/form";
    }

    @PostMapping({ "", "/{id}" })
    public String save(@Valid @ModelAttribute("movie") MovieDto movieDto,
            BindingResult result,
            Model model,
            @PathVariable(required = false) Long id) {

        if (id != null) {
            movieDto.setId(id);
        }

        if (result.hasErrors()) {
            model.addAttribute("genres", MovieGenre.values());
            model.addAttribute("persons", personService.getAll());
            model.addAttribute("colors", Color.values());
            model.addAttribute("mpaaRatings", MpaaRating.values());
            return "movies/form";
        }

        movieService.saveFromDto(movieDto);
        messagingTemplate.convertAndSend("/topic/movies", "update");

        return "redirect:/movies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        movieService.delete(id);

        messagingTemplate.convertAndSend("/topic/movies", "update");
        return "redirect:/movies";
    }
}
