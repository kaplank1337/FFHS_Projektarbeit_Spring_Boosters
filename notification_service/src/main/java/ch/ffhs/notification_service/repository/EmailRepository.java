package ch.ffhs.notification_service.repository;

import ch.ffhs.notification_service.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailLog, Long> {
}
