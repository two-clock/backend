package com.twoclock.gitconnect.domain.board.entity;

import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.global.entity.BaseEntity;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DynamicUpdate
@SQLDelete(sql = "UPDATE board SET is_view = false WHERE id = ?")
@Where(clause = "is_view = true")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private final boolean isView = true;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Builder
    public Board(Member member, String nickname, Category category, String title, String content) {
        this.member = member;
        this.nickname = nickname;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    // 게시판 작성 본인 검증 Method
    public void checkUserId(Long userKey) {
        if (userKey.longValue() != this.member.getId().longValue()) {
            throw new CustomException(ErrorCode.DIFF_USER_BOARD);
        }
    }

    // 게시판 수정 Method
    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addViewCount() {
        this.viewCount++;
    }
}
