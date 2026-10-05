package ru.labs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.PersonDao;
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
}
