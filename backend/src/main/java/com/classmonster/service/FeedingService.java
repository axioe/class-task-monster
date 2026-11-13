package com.classmonster.service;

import com.classmonster.config.AppProperties;
import com.classmonster.domain.Classroom;
import com.classmonster.domain.ExpressionMission;
import com.classmonster.domain.ExpressionOption;
import com.classmonster.domain.FeedingLog;
import com.classmonster.domain.FoodItem;
import com.classmonster.domain.GrowthNews;
import com.classmonster.domain.User;
import com.classmonster.dto.feeding.FeedingDtos.FeedRequest;
import com.classmonster.dto.feeding.FeedingDtos.FeedResponse;
import com.classmonster.dto.food.FoodResponse;
import com.classmonster.dto.monster.MonsterResponse;
import com.classmonster.dto.news.GrowthNewsResponse;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.ClassroomRepository;
import com.classmonster.repository.FeedingLogRepository;
import com.classmonster.util.KoreanJosa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 먹이 주기: 오늘의 표현 미션에서 알맞은 영어 표현을 골라야만 성공한다.
 * 성공 → 먹이 1개 사용, 몬스터 성장 에너지 증가 (기본 1:1)
 * 실패 → 먹이는 그대로, 다시 도전 안내
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedingService {

    private final StudentService studentService;
    private final MissionService missionService;
    private final FoodService foodService;
    private final GrowthService growthService;
    private final ClassroomRepository classroomRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final AppProperties appProperties;

    @Transactional
    public FeedResponse feed(Long studentId, FeedRequest request) {
        User student = studentService.getStudent(studentId);
        // 동시에 여러 학생이 먹이를 줘도 에너지가 누락되지 않도록 반을 잠근다.
        Classroom classroom = classroomRepository.findByIdForUpdate(student.getClassroom().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));

        ExpressionMission mission = missionService.findToday(classroom)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODAY_MISSION_NOT_FOUND));
        ExpressionOption option = mission.findOption(request.optionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));

        if (foodService.countAvailable(student) == 0) {
            throw new BusinessException(ErrorCode.NO_FOOD);
        }

        if (!option.isCorrect()) {
            feedingLogRepository.save(new FeedingLog(classroom, student, mission.getSituation(),
                    option.getExpression(), false, null));
            return new FeedResponse(false,
                    "이 상황에는 다른 표현이 더 잘 어울려요. 다시 골라 볼까요?",
                    null, foodService.countAvailable(student), MonsterResponse.from(classroom), false, null);
        }

        FoodItem food = foodService.consumeOne(student);
        GrowthNews news = growthService.grow(classroom, appProperties.monster().energyPerFood());
        feedingLogRepository.save(new FeedingLog(classroom, student, mission.getSituation(),
                option.getExpression(), true, food.getType()));

        return new FeedResponse(true,
                "\"" + option.getExpression() + "\" " + KoreanJosa.subject(classroom.getMonsterName()) + " 맛있게 먹었어요!",
                FoodResponse.from(food), foodService.countAvailable(student), MonsterResponse.from(classroom),
                news != null, news == null ? null : GrowthNewsResponse.from(news));
    }
}
