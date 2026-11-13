package com.classmonster.dto.news;

import com.classmonster.domain.GrowthNews;

import java.time.LocalDateTime;

public record GrowthNewsResponse(Long id, String message, LocalDateTime createdAt) {

    public static GrowthNewsResponse from(GrowthNews news) {
        return new GrowthNewsResponse(news.getId(), news.getMessage(), news.getCreatedAt());
    }
}
