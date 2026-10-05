package ru.labs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.PersonDao;
import ru.labs.dto.PersonDto;
import ru.labs.model.Person;

@Service
@Transactional
public class PersonService {

    private final PersonDao personDao;

    @Autowired
    public PersonService(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Transactional(readOnly = true)
    public Person getById(Long id) {
        return personDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Person> getAll() {
        return personDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Person> getDirectorsWithoutOscars() {
        return personDao.findDirectorsWithoutOscars();
    }

    public Person create(Person person) {
        personDao.save(person);
        return person;
    }

    public Person update(Person person) {
        return personDao.update(person);
    }

    public void delete(Long id) {
        Person person = personDao.findById(id);
        if (person != null) {
            personDao.delete(person);
        }
    }

    @Transactional
    public void saveFromDto(PersonDto dto) {
        Person person;
        if (dto.getId() != null) {
            person = personDao.findById(dto.getId());
        } else {
            person = new Person();
        }

        person.setName(dto.getName());
        person.setEyeColor(dto.getEyeColor());
        person.setHairColor(dto.getHairColor());
        person.setWeight(dto.getWeight());
        person.setLocation(dto.getLocation());

        if (person.getId() == null) {
            personDao.save(person);
        } else {
            personDao.update(person);
        }
    }

    public PersonDto convertToDto(Person person) {
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
