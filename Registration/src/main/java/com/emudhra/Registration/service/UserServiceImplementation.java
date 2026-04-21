package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ Save user
    @Override
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    // ✅ Get user by email
    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ Get existing user OR create new user after OAuth login
    @Override
    public Users getOrCreateUser(String email, String name) {

        Users user = userRepository.findByEmail(email);

        if (user != null) {
            return user;
        }

        // 🔥 Create new user if not exists
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setUsername(name);
//        newUser.setCreatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }
}