package ru.labs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.UserDao;
import ru.labs.model.AppUser;

@Service
@Transactional
public class AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(String username, String password) {
        if (userDao.findByUsername(username) != null) {
            return false;
        }

        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userDao.save(newUser);

        return true;
    }
}
