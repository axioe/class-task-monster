package com.classmonster.controller;

import com.classmonster.dto.auth.AuthDtos.LoginRequest;
import com.classmonster.dto.auth.AuthDtos.MeResponse;
import com.classmonster.dto.auth.AuthDtos.SignupRequest;
import com.classmonster.dto.auth.AuthDtos.TokenResponse;
import com.classmonster.security.AuthUser;
import com.classmonster.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 교사 회원가입 */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public MeResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signupTeacher(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthUser authUser) {
        return authService.me(authUser.id());
    }
}
