package com.classmonster.controller;

import com.classmonster.dto.mission.MissionDtos.MissionRequest;
import com.classmonster.dto.mission.MissionDtos.MissionResponse;
import com.classmonster.security.AuthUser;
import com.classmonster.service.MissionService;
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
 * 교사 - 날짜별 영어 표현 미션 관리
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherMissionController {

    private final MissionService missionService;

    @PostMapping("/classrooms/{classroomId}/missions")
    @ResponseStatus(HttpStatus.CREATED)
    public MissionResponse create(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId,
                                  @Valid @RequestBody MissionRequest request) {
        return missionService.create(authUser.id(), classroomId, request);
    }

    @GetMapping("/classrooms/{classroomId}/missions")
    public List<MissionResponse> findAll(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long classroomId) {
        return missionService.findByClassroom(authUser.id(), classroomId);
    }

    @PutMapping("/missions/{missionId}")
    public MissionResponse update(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long missionId,
                                  @Valid @RequestBody MissionRequest request) {
        return missionService.update(authUser.id(), missionId, request);
    }

    @DeleteMapping("/missions/{missionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long missionId) {
        missionService.delete(authUser.id(), missionId);
    }
}
