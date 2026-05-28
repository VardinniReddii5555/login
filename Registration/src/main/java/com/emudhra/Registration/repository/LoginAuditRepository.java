package com.emudhra.Registration.repository;

import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginAuditRepository
        extends JpaRepository<LoginAudit, Long> {

    Optional<LoginAudit>
    findTopByUserIdAndLogoutAtIsNullOrderByLoginAtDesc(
            Long userId
    );

    List<LoginAudit>
    findByUserIdOrderByLoginAtDesc(
            Long userId
    );

    List<LoginAudit>
    findByUserIdAndLoginModeOrderByLoginAtDesc(
            Long userId,
            String loginMode
    );

    LoginAudit
    findTopByUserOrderByLoginAtDesc(
            Users user
    );

    LoginAudit findTopByUserIdOrderByLoginAtDesc(
            Long userId
    );
}
