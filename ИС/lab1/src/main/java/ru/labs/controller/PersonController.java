package ru.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.labs.model.Color;
import ru.labs.model.Person;
import ru.labs.service.PersonService;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("persons", personService.getAll());
        return "persons/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("colors", Color.values());
        return "persons/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("person") Person person,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("colors", Color.values());
            return "persons/form";
        }
        personService.create(person);
        return "redirect:/persons";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Person person = personService.getById(id);
        if (person == null)
            return "redirect:/persons";
        model.addAttribute("person", person);
        model.addAttribute("colors", Color.values());
        return "persons/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("person") Person person,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("colors", Color.values());
            return "persons/form";
        }
        Person existing = personService.getById(id);
        if (existing == null)
            return "redirect:/persons";
        person.setId(id);

        personService.update(person);
        return "redirect:/persons";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        personService.delete(id);
        return "redirect:/persons";
    }
}
