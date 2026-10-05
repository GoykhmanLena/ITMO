package ru.labs.controller;

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

import jakarta.validation.Valid;
import ru.labs.dto.PersonDto;
import ru.labs.model.Color;
import ru.labs.model.Person;
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
    public String list(Model model) {
        model.addAttribute("persons", personService.getAll());
        return "persons/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("person", new PersonDto()); // Отправляем пустой DTO
        model.addAttribute("colors", Color.values());
        return "persons/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Person person = personService.getById(id);
        if (person == null)
            return "redirect:/persons";

        model.addAttribute("person", personService.convertToDto(person));
        model.addAttribute("colors", Color.values());
        return "persons/form";
    }

    @PostMapping({ "", "/{id}" })
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("person") PersonDto personDto,
            BindingResult bindingResult,
            Model model) {

        if (id != null) {
            personDto.setId(id);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("colors", Color.values());
            return "persons/form";
        }
        personService.saveFromDto(personDto);

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
