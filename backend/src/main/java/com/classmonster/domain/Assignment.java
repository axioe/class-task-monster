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

import java.time.LocalDateTime;

/**
 * 학생별 맞춤 과제 카드.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssignmentStatus status;

    /** 학생이 확인 요청 시 남기는 메모 (수행 내용) */
    @Column(length = 1000)
    private String submissionNote;

    /** 교사의 짧은 피드백 */
    @Column(length = 500)
    private String teacherComment;

    /** 먹이 지급 여부 (과제당 1회만 지급) */
    @Column(nullable = false)
    private boolean foodGranted;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    public Assignment(Classroom classroom, User student, String title, String description) {
        this.classroom = classroom;
        this.student = student;
        this.title = title;
        this.description = description;
        this.status = AssignmentStatus.IN_PROGRESS;
        this.foodGranted = false;
    }

    public void updateContent(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public boolean canSubmit() {
        return status == AssignmentStatus.IN_PROGRESS || status == AssignmentStatus.REVISION_REQUESTED;
    }

    public void submit(String note, LocalDateTime now) {
        this.status = AssignmentStatus.SUBMITTED;
        this.submissionNote = note;
        this.submittedAt = now;
    }

    public boolean canReview() {
        return status == AssignmentStatus.SUBMITTED;
    }

    public void approve(String comment, LocalDateTime now) {
        this.status = AssignmentStatus.APPROVED;
        this.teacherComment = comment;
        this.reviewedAt = now;
    }

    public void requestRevision(String comment, LocalDateTime now) {
        this.status = AssignmentStatus.REVISION_REQUESTED;
        this.teacherComment = comment;
        this.reviewedAt = now;
    }

    public void markFoodGranted() {
        this.foodGranted = true;
    }

    public boolean isAssignedTo(Long studentId) {
        return student.getId().equals(studentId);
    }
}
