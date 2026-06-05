package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomSamlUserService implements UserDetailsService {

    @Autowired
    private UserRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email)
            throws UsernameNotFoundException {

        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "User not found with email: " + email
            );
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );
    }
}