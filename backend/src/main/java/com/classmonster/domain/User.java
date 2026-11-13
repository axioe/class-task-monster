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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자(교사/학생).
 * 교사는 직접 가입하고, 학생은 교사가 반에 계정을 만들어 준다.
 */
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /** 학생이 속한 반 (교사는 null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private User(String loginId, String password, String nickname, Role role, Classroom classroom) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.classroom = classroom;
    }

    public static User teacher(String loginId, String encodedPassword, String nickname) {
        return new User(loginId, encodedPassword, nickname, Role.TEACHER, null);
    }

    public static User student(String loginId, String encodedPassword, String nickname, Classroom classroom) {
        return new User(loginId, encodedPassword, nickname, Role.STUDENT, classroom);
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public boolean isStudent() {
        return role == Role.STUDENT;
    }
}
