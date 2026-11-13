package com.classmonster.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 표현 미션의 영어 표현 선택지.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpressionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private ExpressionMission mission;

    @Column(nullable = false, length = 100)
    private String expression;

    /** 이 상황에 알맞은 표현인지 */
    @Column(nullable = false)
    private boolean correct;

    public ExpressionOption(String expression, boolean correct) {
        this.expression = expression;
        this.correct = correct;
    }

    void assignTo(ExpressionMission mission) {
        this.mission = mission;
    }
}
