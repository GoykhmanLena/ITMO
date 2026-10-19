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
import ru.labs.dto.PersonDto;
import ru.labs.model.Color;
import ru.labs.service.PersonService;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PersonController(PersonService personService, SimpMessagingTemplate messagingTemplate) {
        this.personService = personService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer pageInput,
            Model model) {
        if (size <= 0) {
            size = 5;
        }

        long total = personService.count();
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

        List<PersonDto> persons = personService.getPage(targetPage, size);

        model.addAttribute("persons", persons);
        model.addAttribute("page", targetPage);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", total);
        return "persons/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        PersonDto person = personService.getById(id);
        if (person == null) {
            return "redirect:/persons";
        }
        model.addAttribute("person", person);
        return "persons/view";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("person", new PersonDto());
        model.addAttribute("colors", Color.values());
        return "persons/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PersonDto person = personService.getById(id);
        if (person == null) {
            return "redirect:/persons";
        }

        model.addAttribute("person", person);
        model.addAttribute("colors", Color.values());
        return "persons/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("person") PersonDto personDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("colors", Color.values());
            return "persons/form";
        }
        personService.create(personDto);

        messagingTemplate.convertAndSend("/topic/persons", "update");

        return "redirect:/persons";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("person") PersonDto personDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            personDto.setId(id);
            model.addAttribute("colors", Color.values());
            return "persons/form";
        }
        personService.update(id, personDto);

        messagingTemplate.convertAndSend("/topic/persons", "update");

        return "redirect:/persons";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        personService.delete(id);

        messagingTemplate.convertAndSend("/topic/persons", "update");

        return "redirect:/persons";
    }
}
