package com.emudhra.Registration.service;

import com.emudhra.Registration.model.PushRequest;
import com.emudhra.Registration.repository.PushRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PushNotificationService {

    @Autowired
    private PushRequestRepository pushRequestRepository;

    // Create a new login request
    public String createLoginRequest(String email) {

        PushRequest request = new PushRequest();

        request.setRequestId(UUID.randomUUID().toString());
        request.setEmail(email);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        pushRequestRepository.save(request);

        return request.getRequestId();
    }

    // Approve request
    public boolean approveRequest(String requestId) {

        Optional<PushRequest> optional =
                pushRequestRepository.findByRequestId(requestId);

        if (optional.isPresent()) {

            PushRequest request = optional.get();

            request.setStatus("APPROVED");
            request.setApprovedAt(LocalDateTime.now());

            pushRequestRepository.save(request);

            return true;
        }

        return false;
    }

    // Reject request
    public boolean rejectRequest(String requestId) {

        Optional<PushRequest> optional =
                pushRequestRepository.findByRequestId(requestId);

        if (optional.isPresent()) {

            PushRequest request = optional.get();

            request.setStatus("REJECTED");

            pushRequestRepository.save(request);

            return true;
        }

        return false;
    }

    // Android checks pending request
    public PushRequest getPendingRequest(String email) {

        return pushRequestRepository
                .findFirstByEmailAndStatusOrderByCreatedAtDesc(
                        email,
                        "PENDING")
                .orElse(null);
    }

    // Portal checks request status
    public String getRequestStatus(String requestId) {

        Optional<PushRequest> optional =
                pushRequestRepository.findByRequestId(requestId);

        if (optional.isPresent()) {
            return optional.get().getStatus();
        }

        return "NOT_FOUND";
    }

}