package com.twoclock.gitconnect.domain.mail.repository;

import com.twoclock.gitconnect.domain.mail.entity.MailForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailFormRepository extends JpaRepository<MailForm, Long> {
}
