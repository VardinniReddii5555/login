package com.emudhra.Registration.repository;

import com.emudhra.Registration.model.UserMfa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMfaRepository
        extends JpaRepository<UserMfa, Long> {

    Optional<UserMfa> findByUsername(String username);

}