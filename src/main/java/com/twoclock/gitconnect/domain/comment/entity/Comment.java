package com.twoclock.gitconnect.domain.comment.entity;

import com.twoclock.gitconnect.domain.board.entity.Board;
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
@SQLDelete(sql = "UPDATE comment SET is_view = false WHERE id = ?")
@Where(clause = "is_view = true")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String nickname;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private final boolean isView = true;

    @Builder
    public Comment(Board board, Member member, String nickname, String content) {
        this.board = board;
        this.member = member;
        this.nickname = nickname;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }

    public void checkUserId(Long userKey) {
        if(userKey.longValue() != this.member.getId().longValue()) {
            throw new CustomException(ErrorCode.DIFF_USER_COMMENT);
        }
    }
}
