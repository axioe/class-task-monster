package com.classmonster.service;

import com.classmonster.domain.User;
import com.classmonster.dto.auth.AuthDtos.LoginRequest;
import com.classmonster.dto.auth.AuthDtos.MeResponse;
import com.classmonster.dto.auth.AuthDtos.SignupRequest;
import com.classmonster.dto.auth.AuthDtos.TokenResponse;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.UserRepository;
import com.classmonster.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /** 교사 회원가입. 학생 계정은 교사가 StudentService 로 만든다. */
    @Transactional
    public MeResponse signupTeacher(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        User teacher = User.teacher(request.loginId(), passwordEncoder.encode(request.password()), request.nickname());
        return MeResponse.from(userRepository.save(teacher));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        String token = tokenProvider.createAccessToken(user.getId(), user.getRole());
        return TokenResponse.bearer(token, tokenProvider.getValiditySeconds(), MeResponse.from(user));
    }

    public MeResponse me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return MeResponse.from(user);
    }
}
