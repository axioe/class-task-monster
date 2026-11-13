package com.classmonster.dto.food;

import com.classmonster.domain.FoodItem;
import com.classmonster.domain.FoodType;

public record FoodResponse(Long id, FoodType type, String label) {

    public static FoodResponse from(FoodItem food) {
        return new FoodResponse(food.getId(), food.getType(), food.getType().getLabel());
    }
}
