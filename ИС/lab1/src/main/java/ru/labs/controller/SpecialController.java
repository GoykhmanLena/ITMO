package ru.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.labs.model.MovieGenre;
import ru.labs.service.MovieService;
import ru.labs.service.PersonService;

@Controller
@RequestMapping("/special")
public class SpecialController {

    private final MovieService movieService;
    private final PersonService personService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SpecialController(MovieService movieService, PersonService personService,
            SimpMessagingTemplate messagingTemplate) {
        this.movieService = movieService;
        this.personService = personService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("genres", MovieGenre.values());
        return "special/index";
    }

    @PostMapping("/delete-by-genre")
    public String deleteByGenre(@RequestParam MovieGenre genre) {
        movieService.deleteByGenre(genre);

        messagingTemplate.convertAndSend("/topic/movies", "update");
        return "redirect:/movies";
    }

    @GetMapping("/search-by-name")
    public String searchByName(@RequestParam String prefix, Model model) {
        model.addAttribute("moviesResult", movieService.findByNameStartingWith(prefix));
        model.addAttribute("genres", MovieGenre.values());
        return "special/index";
    }

    @GetMapping("/genre-less-than")
    public String genreLessThan(@RequestParam MovieGenre genre, Model model) {
        model.addAttribute("moviesResult", movieService.findByGenreLessThan(genre));
        model.addAttribute("genres", MovieGenre.values());
        return "special/index";
    }

    @GetMapping("/directors-no-oscars")
    public String directorsNoOscars(Model model) {
        model.addAttribute("personsResult", personService.getDirectorsWithoutOscars());
        model.addAttribute("genres", MovieGenre.values());
        return "special/index";
    }

    @PostMapping("/redistribute-oscars")
    public String redistributeOscars(@RequestParam MovieGenre from, @RequestParam MovieGenre to) {
        movieService.redistributeOscars(from, to);

        messagingTemplate.convertAndSend("/topic/movies", "update");
        return "redirect:/movies";
    }
}
