package com.twoclock.gitconnect.domain.mail.repository;

import com.twoclock.gitconnect.domain.mail.entity.MailSendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailSendHistoryRepository extends JpaRepository<MailSendHistory, Long> {
}
