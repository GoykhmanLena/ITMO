package ru.labs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import ru.labs.dto.PersonCreateDto;
import ru.labs.model.Location;
import ru.labs.model.Person;
import ru.labs.service.LocationService;
import ru.labs.service.PersonService;

@Controller
public class PersonController {

    private final PersonService personService;
    private final LocationService locationService;

    public PersonController(
            PersonService personService,
            LocationService locationService) {
        this.personService = personService;
        this.locationService = locationService;
    }

    @GetMapping("/persons")
    public String persons(Model model) {
        model.addAttribute("persons", personService.findAll());
        return "persons";
    }

    @GetMapping("/persons/new")
    public String newPersonForm(Model model) {
        model.addAttribute("person", new PersonCreateDto());
        return "person-form";
    }

    @PostMapping("/persons")
    public String createPerson(
            @Valid @ModelAttribute("person") PersonCreateDto dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "person-form";
        }

        Location location = new Location();
        location.setX(dto.getLocationX());
        location.setY(dto.getLocationY());
        location.setName(dto.getLocationName());

        Location savedLocation = locationService.create(location);

        Person person = new Person();
        person.setName(dto.getName());
        person.setEyeColor(dto.getEyeColor());
        person.setHairColor(dto.getHairColor());
        person.setBirthday(dto.getBirthday());
        person.setWeight(dto.getWeight());
        person.setLocation(savedLocation);

        personService.create(person);

        return "redirect:/persons";
    }
}
