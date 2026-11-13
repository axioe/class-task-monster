package com.classmonster.security;

import com.classmonster.domain.Role;

/**
 * JWT 에서 꺼낸 로그인 사용자 정보. 컨트롤러에서 @AuthenticationPrincipal 로 받는다.
 */
public record AuthUser(Long id, Role role) {
}
