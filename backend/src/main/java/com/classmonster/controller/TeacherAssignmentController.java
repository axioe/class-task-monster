package com.classmonster.controller;

import com.classmonster.domain.AssignmentStatus;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentCreateRequest;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentResponse;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentReviewRequest;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentReviewResponse;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentUpdateRequest;
import com.classmonster.security.AuthUser;
import com.classmonster.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 교사 - 과제 지정 → 수행 확인 → 피드백
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherAssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/classrooms/{classroomId}/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId,
                                     @Valid @RequestBody AssignmentCreateRequest request) {
        return assignmentService.create(authUser.id(), classroomId, request);
    }

    /** status 를 주면 해당 상태만 (예: SUBMITTED = 확인 대기) */
    @GetMapping("/classrooms/{classroomId}/assignments")
    public List<AssignmentResponse> findAll(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId,
                                            @RequestParam(required = false) AssignmentStatus status) {
        return assignmentService.findByClassroom(authUser.id(), classroomId, status);
    }

    @PutMapping("/assignments/{assignmentId}")
    public AssignmentResponse update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long assignmentId,
                                     @Valid @RequestBody AssignmentUpdateRequest request) {
        return assignmentService.update(authUser.id(), assignmentId, request);
    }

    @DeleteMapping("/assignments/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long assignmentId) {
        assignmentService.delete(authUser.id(), assignmentId);
    }

    @PostMapping("/assignments/{assignmentId}/review")
    public AssignmentReviewResponse review(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long assignmentId,
                                           @Valid @RequestBody AssignmentReviewRequest request) {
        return assignmentService.review(authUser.id(), assignmentId, request);
    }
}
