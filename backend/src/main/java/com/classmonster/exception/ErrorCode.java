package com.classmonster.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 서비스 전체에서 사용하는 에러 코드. 메시지는 화면에 그대로 보여줄 수 있는 문장으로 작성한다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값을 확인해 주세요."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요해요."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없어요."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "잠시 후 다시 시도해 주세요."),

    // 인증/계정
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 맞지 않아요."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디예요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없어요."),
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "학생을 찾을 수 없어요."),

    // 반
    CLASSROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "반을 찾을 수 없어요."),

    // 과제
    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "과제를 찾을 수 없어요."),
    ASSIGNMENT_NOT_SUBMITTABLE(HttpStatus.CONFLICT, "지금은 확인 요청을 할 수 없는 과제예요."),
    ASSIGNMENT_NOT_REVIEWABLE(HttpStatus.CONFLICT, "확인 요청된 과제만 확인할 수 있어요."),

    // 표현 미션
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "표현 미션을 찾을 수 없어요."),
    TODAY_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘의 표현 미션이 아직 없어요."),
    DUPLICATE_MISSION_DATE(HttpStatus.CONFLICT, "해당 날짜에 이미 표현 미션이 있어요."),
    MISSION_NEEDS_CORRECT_OPTION(HttpStatus.BAD_REQUEST, "알맞은 표현을 1개 이상 지정해 주세요."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "선택한 표현을 찾을 수 없어요."),

    // 먹이
    NO_FOOD(HttpStatus.CONFLICT, "먹이가 없어요. 과제를 완료하고 선생님 확인을 받아 보세요.");

    private final HttpStatus status;
    private final String message;
}
