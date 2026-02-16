package com.emudhra.Registration.repository;
import com.emudhra.Registration.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,String> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
