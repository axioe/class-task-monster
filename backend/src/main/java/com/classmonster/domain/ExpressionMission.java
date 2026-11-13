package com.classmonster.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 교사가 날짜별로 내는 영어 표현 미션. (예: "칭찬해 줘" → "Good job!")
 * 학생은 알맞은 표현을 골라야 먹이 주기에 성공한다.
 */
@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"classroom_id", "mission_date"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpressionMission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(name = "mission_date", nullable = false)
    private LocalDate missionDate;

    /** 상황 설명 (한국어), 예: "몬스터를 칭찬해 줘" */
    @Column(nullable = false, length = 200)
    private String situation;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ExpressionOption> options = new ArrayList<>();

    public ExpressionMission(Classroom classroom, LocalDate missionDate, String situation) {
        this.classroom = classroom;
        this.missionDate = missionDate;
        this.situation = situation;
    }

    public void update(LocalDate missionDate, String situation) {
        this.missionDate = missionDate;
        this.situation = situation;
    }

    public void replaceOptions(List<ExpressionOption> newOptions) {
        this.options.clear();
        newOptions.forEach(this::addOption);
    }

    public void addOption(ExpressionOption option) {
        option.assignTo(this);
        this.options.add(option);
    }

    public Optional<ExpressionOption> findOption(Long optionId) {
        return options.stream().filter(o -> o.getId().equals(optionId)).findFirst();
    }
}
