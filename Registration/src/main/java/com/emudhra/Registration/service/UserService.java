package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;

public interface UserService {

    Users saveUser(Users user);

    Users getUserByEmail(String email);

    Users getOrCreateUser(String email, String name);

}