package ru.labs.service;

import java.util.List;
import java.util.stream.Collectors;

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
    public PersonDto getById(Long id) {
        Person person = personDao.findById(id);
        return convertToDto(person);
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getAll() {
        return personDao.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getPage(int page, int size) {
        return personDao.findAll(page, size).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long count() {
        return personDao.count();
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getDirectorsWithoutOscars() {
        return personDao.findDirectorsWithoutOscars().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PersonDto create(PersonDto dto) {
        Person person = new Person();
        copyEditableFields(dto, person);
        personDao.save(person);
        return convertToDto(person);
    }

    public PersonDto update(Long id, PersonDto dto) {
        Person person = personDao.findById(id);
        if (person == null) {
            return null;
        }
        copyEditableFields(dto, person);
        personDao.update(person);
        return convertToDto(person);
    }

    public void delete(Long id) {
        Person person = personDao.findById(id);
        if (person != null) {
            personDao.delete(person);
        }
    }

    public Person resolveOrCreate(PersonDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getId() != null) {
                return personDao.findById(dto.getId());
            }

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            Person newPerson = new Person();
            copyEditableFields(dto, newPerson);
            personDao.save(newPerson);
            return newPerson;
        }

        return null;
    }

    private void copyEditableFields(PersonDto source, Person target) {
        target.setName(source.getName());
        target.setEyeColor(source.getEyeColor());
        target.setHairColor(source.getHairColor());
        target.setWeight(source.getWeight());
        target.setLocation(source.getLocation());
        target.setBirthday(source.getBirthday());
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
        dto.setBirthday(person.getBirthday());
        return dto;
    }
}
