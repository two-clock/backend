package com.twoclock.gitconnect.global.model;

public record FileDto(
        String uuid,
        String originalName,
        String fileExtension,
        int fileSize
) {
}
