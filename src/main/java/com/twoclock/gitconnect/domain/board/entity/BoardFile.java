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
    private String uuid;

    @Column(nullable = false)
    private String savePath;

    @Column(nullable = false)
    private String extension;

    @Builder
    public BoardFile(Board board, String originalName, String uuid, String savePath, String extension) {
        this.board = board;
        this.originalName = originalName;
        this.uuid = uuid;
        this.savePath = savePath;
        this.extension = extension;
    }
}
