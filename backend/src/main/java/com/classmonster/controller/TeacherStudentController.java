package com.classmonster.controller;

import com.classmonster.dto.student.StudentDtos.StudentCreateRequest;
import com.classmonster.dto.student.StudentDtos.StudentResponse;
import com.classmonster.dto.student.StudentDtos.StudentUpdateRequest;
import com.classmonster.security.AuthUser;
import com.classmonster.service.StudentService;
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
 * 교사 - 반 학생 계정 관리
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherStudentController {

    private final StudentService studentService;

    @PostMapping("/classrooms/{classroomId}/students")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId,
                                  @Valid @RequestBody StudentCreateRequest request) {
        return studentService.create(authUser.id(), classroomId, request);
    }

    @GetMapping("/classrooms/{classroomId}/students")
    public List<StudentResponse> findAll(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId) {
        return studentService.findAll(authUser.id(), classroomId);
    }

    @PutMapping("/students/{studentId}")
    public StudentResponse update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long studentId,
                                  @Valid @RequestBody StudentUpdateRequest request) {
        return studentService.update(authUser.id(), studentId, request);
    }

    @DeleteMapping("/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long studentId) {
        studentService.delete(authUser.id(), studentId);
    }
}
