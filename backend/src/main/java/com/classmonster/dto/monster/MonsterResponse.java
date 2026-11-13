package com.classmonster.dto.monster;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.GrowthStage;

/**
 * 공동 몬스터 상태. 화면의 성장 에너지 바(예: 18 / 30)와 단계 표시에 사용한다.
 *
 * @param nextStageEnergy 다음 단계에 필요한 에너지. 마지막 단계면 null
 */
public record MonsterResponse(String name, int energy, GrowthStage stage, String stageLabel,
                              GrowthStage nextStage, Integer nextStageEnergy) {

    public static MonsterResponse from(Classroom classroom) {
        GrowthStage stage = classroom.getStage();
        GrowthStage next = stage.next();
        return new MonsterResponse(classroom.getMonsterName(), classroom.getGrowthEnergy(), stage, stage.getLabel(),
                next, next == null ? null : next.getMinEnergy());
    }
}
