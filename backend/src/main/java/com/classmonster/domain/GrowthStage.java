package com.classmonster.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 공동 몬스터의 성장 단계: 알 → 아기 → 성장.
 * minEnergy 이상의 성장 에너지를 모으면 해당 단계가 된다.
 */
@Getter
@RequiredArgsConstructor
public enum GrowthStage {
    EGG("알", 0),
    BABY("아기", 10),
    GROWN("성장", 30);

    private final String label;
    private final int minEnergy;

    public static GrowthStage fromEnergy(int energy) {
        GrowthStage result = EGG;
        for (GrowthStage stage : values()) {
            if (energy >= stage.minEnergy) {
                result = stage;
            }
        }
        return result;
    }

    /** 다음 단계. 마지막 단계면 null */
    public GrowthStage next() {
        int nextIndex = ordinal() + 1;
        return nextIndex < values().length ? values()[nextIndex] : null;
    }
}
