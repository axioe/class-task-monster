package com.classmonster.dto.student;

import com.classmonster.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class StudentDtos {

    private StudentDtos() {
    }

    /** 교사가 반에 학생 계정을 만든다. */
    public record StudentCreateRequest(
            @NotBlank @Size(min = 4, max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, _ 만 사용할 수 있어요.")
            String loginId,
            @NotBlank @Size(min = 4, max = 64) String password,
            @NotBlank @Size(max = 30) String nickname) {
    }

    /** password 가 비어 있으면 비밀번호는 바꾸지 않는다. */
    public record StudentUpdateRequest(
            @NotBlank @Size(max = 30) String nickname,
            @Size(min = 4, max = 64) String password) {
    }

    /**
     * 교사 화면용 학생 정보. 파일럿 분석 지표(과제 확인 수, 표현 정확도)를 함께 담는다.
     */
    public record StudentResponse(Long id, String loginId, String nickname, long foodCount,
                                  long approvedAssignmentCount, long feedingAttemptCount, long feedingSuccessCount) {
        public static StudentResponse of(User student, long foodCount, long approvedAssignmentCount,
                                         long feedingAttemptCount, long feedingSuccessCount) {
            return new StudentResponse(student.getId(), student.getLoginId(), student.getNickname(), foodCount,
                    approvedAssignmentCount, feedingAttemptCount, feedingSuccessCount);
        }
    }
}
