package com.classmonster.controller;

import com.classmonster.dto.assignment.AssignmentDtos.AssignmentResponse;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentSubmitRequest;
import com.classmonster.dto.dashboard.StudentDashboardResponse;
import com.classmonster.dto.dashboard.StudentGrowthResponse;
import com.classmonster.dto.feeding.FeedingDtos.FeedRequest;
import com.classmonster.dto.feeding.FeedingDtos.FeedResponse;
import com.classmonster.security.AuthUser;
import com.classmonster.service.AssignmentService;
import com.classmonster.service.FeedingService;
import com.classmonster.service.StudentDashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 학생 - 메인 화면, 과제 확인 요청, 먹이 주기, 성장 기록
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentDashboardService dashboardService;
    private final AssignmentService assignmentService;
    private final FeedingService feedingService;

    /** 학생 메인 화면 데이터 (몬스터, 오늘 도전, 먹이, 오늘의 표현, 성장 소식) */
    @GetMapping("/dashboard")
    public StudentDashboardResponse dashboard(@AuthenticationPrincipal AuthUser authUser) {
        return dashboardService.getDashboard(authUser.id());
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public AssignmentResponse submit(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long assignmentId,
                                     @Valid @RequestBody(required = false) AssignmentSubmitRequest request) {
        return assignmentService.submit(authUser.id(), assignmentId, request);
    }

    @PostMapping("/feed")
    public FeedResponse feed(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody FeedRequest request) {
        return feedingService.feed(authUser.id(), request);
    }

    @GetMapping("/growth")
    public StudentGrowthResponse growth(@AuthenticationPrincipal AuthUser authUser) {
        return dashboardService.getGrowth(authUser.id());
    }
}
