package com.classmonster.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.random.RandomGenerator;

/**
 * 먹이 종류. 기능상 차이는 없고, 과제 확인 시 무작위로 지급된다.
 */
@Getter
@RequiredArgsConstructor
public enum FoodType {
    STRAWBERRY("딸기"),
    GRAPE("포도"),
    APPLE("사과"),
    BANANA("바나나"),
    COOKIE("쿠키");

    private final String label;

    public static FoodType random(RandomGenerator random) {
        FoodType[] types = values();
        return types[random.nextInt(types.length)];
    }
}
