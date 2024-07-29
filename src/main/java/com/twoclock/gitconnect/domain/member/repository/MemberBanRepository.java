package com.twoclock.gitconnect.domain.member.repository;

import com.twoclock.gitconnect.domain.member.entity.MemberBan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBanRepository extends JpaRepository<MemberBan, Long> {
}
