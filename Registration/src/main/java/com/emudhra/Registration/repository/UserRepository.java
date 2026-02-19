package com.emudhra.Registration.repository;
import com.emudhra.Registration.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Users findByEmail(String email);
    Users findByUsername(String username);

}
