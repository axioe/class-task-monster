package com.classmonster.controller;

import com.classmonster.dto.classroom.ClassroomDtos.ClassroomRequest;
import com.classmonster.dto.classroom.ClassroomDtos.ClassroomResponse;
import com.classmonster.security.AuthUser;
import com.classmonster.service.ClassroomService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 교사 - 반 관리
 */
@RestController
@RequestMapping("/api/teacher/classrooms")
@RequiredArgsConstructor
public class TeacherClassroomController {

    private final ClassroomService classroomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassroomResponse create(@AuthenticationPrincipal AuthUser authUser,
                                    @Valid @RequestBody ClassroomRequest request) {
        return classroomService.create(authUser.id(), request);
    }

    @GetMapping
    public List<ClassroomResponse> findMine(@AuthenticationPrincipal AuthUser authUser) {
        return classroomService.findMine(authUser.id());
    }

    @GetMapping("/{classroomId}")
    public ClassroomResponse findOne(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId) {
        return classroomService.findOne(authUser.id(), classroomId);
    }

    @PutMapping("/{classroomId}")
    public ClassroomResponse update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId,
                                    @Valid @RequestBody ClassroomRequest request) {
        return classroomService.update(authUser.id(), classroomId, request);
    }

    @DeleteMapping("/{classroomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId) {
        classroomService.delete(authUser.id(), classroomId);
    }
}
