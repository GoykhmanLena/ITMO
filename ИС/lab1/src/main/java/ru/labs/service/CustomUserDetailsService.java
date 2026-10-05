package ru.labs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.labs.dao.UserDao;
import ru.labs.model.AppUser;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userDao.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .roles("USER")
                .build();
    }
}
