package com.emudhra.Registration.repository;

import com.emudhra.Registration.model.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {
    Optional<LoginAudit> findTopByUserIdAndLogoutAtIsNullOrderByLoginAtDesc(Long userId);
    LoginAudit findBySessionId(String sessionId);
}