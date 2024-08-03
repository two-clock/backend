package com.twoclock.gitconnect.global.dummy;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class DummyDevlnit {

    // 프로덕트 출시 전에 개발자들이 사용할 더미 데이터를 초기화하는 클래스
    @Profile("local")
    @Bean
    CommandLineRunner init(MemberRepository memberRepository) {
        return (args) -> {
            log.info("Dummy Data Init");
            Member member = Member.builder()
                    .nickname("테스트 유저")
                    .token("testToken")
                    .profileImageUrl("/uploads/profile/test.jpg")
                    .role(Role.ROLE_USER)
                    .build();
            memberRepository.save(member);
        };
    }

}
