package com.twoclock.gitconnect.domain.member.repository;

import com.twoclock.gitconnect.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
