package com.twoclock.gitconnect.domain.board.entity;

import com.twoclock.gitconnect.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class BoardFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, unique = true)
    private String fileUrl;


    @Builder
    public BoardFile(Board board, String originalName, String fileUrl) {
        this.board = board;
        this.originalName = originalName;
        this.fileUrl = fileUrl;
    }
}
