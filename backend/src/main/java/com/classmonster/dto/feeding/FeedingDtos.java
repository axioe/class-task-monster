package com.classmonster.dto.feeding;

import com.classmonster.domain.FeedingLog;
import com.classmonster.dto.food.FoodResponse;
import com.classmonster.dto.monster.MonsterResponse;
import com.classmonster.dto.news.GrowthNewsResponse;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class FeedingDtos {

    private FeedingDtos() {
    }

    /** 오늘의 미션에서 고른 영어 표현 */
    public record FeedRequest(@NotNull Long optionId) {
    }

    /**
     * 먹이 주기 결과.
     *
     * @param success      알맞은 표현을 골라 먹이 주기에 성공했는지
     * @param consumedFood 성공 시 사용한 먹이 (실패 시 null)
     * @param stageChanged 이번 먹이로 성장 단계가 바뀌었는지
     * @param news         단계가 바뀌어 새로 생긴 성장 소식 (없으면 null)
     */
    public record FeedResponse(boolean success, String message, FoodResponse consumedFood, long remainingFoodCount,
                               MonsterResponse monster, boolean stageChanged, GrowthNewsResponse news) {
    }

    public record FeedingLogResponse(Long id, String situation, String chosenExpression, boolean success,
                                     String foodLabel, LocalDateTime createdAt) {
        public static FeedingLogResponse from(FeedingLog log) {
            return new FeedingLogResponse(log.getId(), log.getSituation(), log.getChosenExpression(), log.isSuccess(),
                    log.getFoodType() == null ? null : log.getFoodType().getLabel(), log.getCreatedAt());
        }
    }
}
