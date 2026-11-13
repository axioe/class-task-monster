package com.classmonster.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 먹이 주기 시도 기록 (성공/실패 모두 저장).
 * 표현 사용 정확도 등 수업 후 분석에 사용한다.
 * 미션이 수정/삭제돼도 기록이 남도록 미션 내용은 문자열로 저장한다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedingLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, length = 200)
    private String situation;

    @Column(nullable = false, length = 100)
    private String chosenExpression;

    @Column(nullable = false)
    private boolean success;

    /** 성공 시 사용한 먹이 종류 (실패 시 null) */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FoodType foodType;

    public FeedingLog(Classroom classroom, User student, String situation,
                      String chosenExpression, boolean success, FoodType foodType) {
        this.classroom = classroom;
        this.student = student;
        this.situation = situation;
        this.chosenExpression = chosenExpression;
        this.success = success;
        this.foodType = foodType;
    }
}
