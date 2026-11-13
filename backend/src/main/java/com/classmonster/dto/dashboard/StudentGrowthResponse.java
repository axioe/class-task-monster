package com.classmonster.dto.dashboard;

import com.classmonster.dto.feeding.FeedingDtos.FeedingLogResponse;
import com.classmonster.dto.monster.MonsterResponse;
import com.classmonster.dto.news.GrowthNewsResponse;

import java.util.List;

/**
 * 학생 "성장 기록" 화면: 우리 반 성장 소식 전체 + 내가 준 먹이 기록.
 */
public record StudentGrowthResponse(MonsterResponse monster, List<GrowthNewsResponse> news,
                                    List<FeedingLogResponse> myFeedings, long mySuccessCount) {
}
