package com.classmonster.dto.assignment;

import com.classmonster.domain.Assignment;
import com.classmonster.domain.AssignmentStatus;
import com.classmonster.dto.food.FoodResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class AssignmentDtos {

    private AssignmentDtos() {
    }

    /** 교사가 학생에게 과제를 지정 */
    public record AssignmentCreateRequest(
            @NotNull Long studentId,
            @NotBlank @Size(max = 100) String title,
            @Size(max = 1000) String description) {
    }

    public record AssignmentUpdateRequest(
            @NotBlank @Size(max = 100) String title,
            @Size(max = 1000) String description) {
    }

    /** 학생의 확인 요청 */
    public record AssignmentSubmitRequest(@Size(max = 1000) String note) {
    }

    /** 교사의 수행 확인: approved=true 면 확인 완료(먹이 지급), false 면 다시 도전 */
    public record AssignmentReviewRequest(
            @NotNull Boolean approved,
            @Size(max = 500) String comment) {
    }

    public record AssignmentResponse(Long id, Long studentId, String studentNickname, String title, String description,
                                     AssignmentStatus status, String statusLabel, String submissionNote,
                                     String teacherComment, boolean foodGranted, LocalDateTime submittedAt,
                                     LocalDateTime reviewedAt, LocalDateTime createdAt) {
        public static AssignmentResponse from(Assignment a) {
            return new AssignmentResponse(a.getId(), a.getStudent().getId(), a.getStudent().getNickname(),
                    a.getTitle(), a.getDescription(), a.getStatus(), a.getStatus().getLabel(),
                    a.getSubmissionNote(), a.getTeacherComment(), a.isFoodGranted(), a.getSubmittedAt(),
                    a.getReviewedAt(), a.getCreatedAt());
        }
    }

    /** grantedFood: 이번 확인으로 지급된 먹이 (없으면 null) */
    public record AssignmentReviewResponse(AssignmentResponse assignment, FoodResponse grantedFood) {
    }
}
