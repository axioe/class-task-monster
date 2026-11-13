package com.classmonster.domain;

public enum Role {
    TEACHER,
    STUDENT;

    /** Spring Security 권한 이름 (ROLE_ 접두사) */
    public String authority() {
        return "ROLE_" + name();
    }
}
