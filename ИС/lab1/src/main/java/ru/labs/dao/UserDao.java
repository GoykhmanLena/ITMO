package ru.labs.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.labs.model.AppUser;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    public void save(AppUser user) {
        em.persist(user);
    }

    public AppUser findByUsername(String username) {
        List<AppUser> users = em.createQuery("SELECT u FROM AppUser u WHERE u.username = :uname", AppUser.class)
                .setParameter("uname", username)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
}
