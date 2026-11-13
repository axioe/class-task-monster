package com.classmonster.service;

import com.classmonster.domain.FoodItem;
import com.classmonster.domain.FoodType;
import com.classmonster.domain.User;
import com.classmonster.dto.food.FoodResponse;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * 학생의 먹이 지급·조회·사용.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private final FoodItemRepository foodItemRepository;
    private final RandomGenerator randomGenerator;
    private final Clock clock;

    /** 확인된 과제 1개당 무작위 종류의 먹이 1개를 지급한다. */
    @Transactional
    public FoodItem grantRandomFood(User student) {
        return foodItemRepository.save(new FoodItem(student, FoodType.random(randomGenerator)));
    }

    public List<FoodResponse> findAvailable(User student) {
        return foodItemRepository.findAllByOwnerAndConsumedFalseOrderByIdAsc(student).stream()
                .map(FoodResponse::from)
                .toList();
    }

    public long countAvailable(User student) {
        return foodItemRepository.countByOwnerAndConsumedFalse(student);
    }

    /** 가장 먼저 받은 먹이 1개를 사용한다. */
    @Transactional
    public FoodItem consumeOne(User student) {
        FoodItem food = foodItemRepository.findFirstByOwnerAndConsumedFalseOrderByIdAsc(student)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_FOOD));
        food.consume(LocalDateTime.now(clock));
        return food;
    }
}
