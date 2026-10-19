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
import ru.labs.model.MovieGenre;
import ru.labs.model.MpaaRating;
import ru.labs.service.MovieService;
import ru.labs.service.PersonService;

@Controller
@RequestMapping("/movies")
public class MovieController {

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
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer pageInput,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) String sort,
            Model model) {
        if (size <= 0) {
            size = 5;
        }

        long total = movieService.count(nameFilter);
        int totalPages = (int) Math.ceil((double) total / size);

        int targetPage = page;
        if (pageInput != null) {
            targetPage = pageInput - 1;
        }
        if (targetPage < 0) {
            targetPage = 0;
        }
        if (totalPages > 0 && targetPage >= totalPages) {
            targetPage = totalPages - 1;
        }

        List<MovieDto> movies = movieService.getPage(targetPage, size, nameFilter, sort);

        model.addAttribute("movies", movies);
        model.addAttribute("page", targetPage);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", total);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("sort", sort);
        return "movies/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        MovieDto movie = movieService.getById(id);
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
        MovieDto movie = movieService.getById(id);
        if (movie == null)
            return "redirect:/movies";

        model.addAttribute("movie", movie);
        model.addAttribute("genres", MovieGenre.values());
        model.addAttribute("persons", personService.getAll());
        model.addAttribute("colors", Color.values());
        model.addAttribute("mpaaRatings", MpaaRating.values());
        return "movies/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("movie") MovieDto movieDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("genres", MovieGenre.values());
            model.addAttribute("persons", personService.getAll());
            model.addAttribute("colors", Color.values());
            model.addAttribute("mpaaRatings", MpaaRating.values());
            return "movies/form";
        }

        movieService.create(movieDto);
        messagingTemplate.convertAndSend("/topic/movies", "update");

        return "redirect:/movies";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("movie") MovieDto movieDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            movieDto.setId(id);
            model.addAttribute("genres", MovieGenre.values());
            model.addAttribute("persons", personService.getAll());
            model.addAttribute("colors", Color.values());
            model.addAttribute("mpaaRatings", MpaaRating.values());
            return "movies/form";
        }

        movieService.update(id, movieDto);
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
