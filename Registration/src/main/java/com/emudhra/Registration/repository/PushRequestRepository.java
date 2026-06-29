package com.emudhra.Registration.repository;

import com.emudhra.Registration.model.PushRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushRequestRepository extends JpaRepository<PushRequest, Long> {

    Optional<PushRequest> findByRequestId(String requestId);

    Optional<PushRequest> findFirstByEmailAndStatusOrderByCreatedAtDesc(
            String email,
            String status
    );

}