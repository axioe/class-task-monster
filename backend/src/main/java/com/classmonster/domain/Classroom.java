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
 * 반. 반마다 공동 몬스터 하나를 함께 키운다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Classroom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String monsterName;

    @Column(nullable = false)
    private int growthEnergy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GrowthStage stage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    public Classroom(String name, String monsterName, User teacher) {
        this.name = name;
        this.monsterName = monsterName;
        this.teacher = teacher;
        this.growthEnergy = 0;
        this.stage = GrowthStage.EGG;
    }

    public void update(String name, String monsterName) {
        this.name = name;
        this.monsterName = monsterName;
    }

    /**
     * 성장 에너지를 더한다.
     * @return 성장 단계가 바뀌었으면 true
     */
    public boolean addEnergy(int amount) {
        GrowthStage before = this.stage;
        this.growthEnergy += amount;
        this.stage = GrowthStage.fromEnergy(this.growthEnergy);
        return before != this.stage;
    }

    public boolean isOwnedBy(Long teacherId) {
        return teacher.getId().equals(teacherId);
    }
}
