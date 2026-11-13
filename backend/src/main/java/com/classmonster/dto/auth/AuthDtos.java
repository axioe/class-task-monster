package com.classmonster.dto.auth;

import com.classmonster.domain.Role;
import com.classmonster.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 인증 관련 요청/응답 DTO 모음.
 */
public final class AuthDtos {

    private AuthDtos() {
    }

    /** 교사 회원가입 */
    public record SignupRequest(
            @NotBlank @Size(min = 4, max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, _ 만 사용할 수 있어요.")
            String loginId,
            @NotBlank @Size(min = 8, max = 64) String password,
            @NotBlank @Size(max = 30) String nickname) {
    }

    public record LoginRequest(
            @NotBlank String loginId,
            @NotBlank String password) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn, MeResponse user) {
        public static TokenResponse bearer(String accessToken, long expiresIn, MeResponse user) {
            return new TokenResponse(accessToken, "Bearer", expiresIn, user);
        }
    }

    public record MeResponse(Long id, String loginId, String nickname, Role role,
                             Long classroomId, String classroomName) {
        public static MeResponse from(User user) {
            var classroom = user.getClassroom();
            return new MeResponse(user.getId(), user.getLoginId(), user.getNickname(), user.getRole(),
                    classroom == null ? null : classroom.getId(),
                    classroom == null ? null : classroom.getName());
        }
    }
}
