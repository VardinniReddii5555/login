package com.emudhra.Registration.repository;

import com.emudhra.Registration.model.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository
        extends JpaRepository<UserOtp, Long> {

    Optional<UserOtp> findTopByUsernameOrderByIdDesc(String username);
    Optional<UserOtp> findTopByEmailOrderByCreatedAtDesc(String email);
}