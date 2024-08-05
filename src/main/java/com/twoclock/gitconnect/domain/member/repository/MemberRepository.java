package com.twoclock.gitconnect.domain.member.repository;

import com.twoclock.gitconnect.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLogin(String login);
}
