package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users save(Users user) {
        return userRepository.save(user);
    }
}