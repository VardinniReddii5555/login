package com.emudhra.Registration.repository;
import com.emudhra.Registration.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Users findByEmail(String email);
    Users findByUsername(String username);
//    Optional<Users> findByEmail(String email);
}
