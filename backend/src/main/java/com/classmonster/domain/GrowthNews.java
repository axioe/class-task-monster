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
 * 우리 반 성장 소식 (예: "함께 모은 먹이로 작은 날개가 생겼어요!")
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GrowthNews extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(nullable = false, length = 200)
    private String message;

    public GrowthNews(Classroom classroom, String message) {
        this.classroom = classroom;
        this.message = message;
    }
}
