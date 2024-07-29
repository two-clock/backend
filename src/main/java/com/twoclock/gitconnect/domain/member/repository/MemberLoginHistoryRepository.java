package com.twoclock.gitconnect.domain.member.repository;

import com.twoclock.gitconnect.domain.member.entity.MemberLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLoginHistoryRepository extends JpaRepository<MemberLoginHistory, Long> {
}
