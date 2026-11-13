package com.classmonster.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 과제 상태 흐름: 도전 중 → 확인 요청 → 선생님 확인 완료 / 다시 도전
 */
@Getter
@RequiredArgsConstructor
public enum AssignmentStatus {
    IN_PROGRESS("도전 중"),
    SUBMITTED("확인 요청"),
    APPROVED("선생님 확인 완료"),
    REVISION_REQUESTED("다시 도전");

    private final String label;
}
