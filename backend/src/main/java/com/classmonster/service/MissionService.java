package com.classmonster.service;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.ExpressionMission;
import com.classmonster.domain.ExpressionOption;
import com.classmonster.dto.mission.MissionDtos.MissionRequest;
import com.classmonster.dto.mission.MissionDtos.MissionResponse;
import com.classmonster.dto.mission.MissionDtos.OptionRequest;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.ExpressionMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 오늘의 표현 미션: 교사가 날짜별로 상황과 영어 표현 선택지(정답 포함)를 등록한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final ExpressionMissionRepository missionRepository;
    private final ClassroomService classroomService;
    private final Clock clock;

    @Transactional
    public MissionResponse create(Long teacherId, Long classroomId, MissionRequest request) {
        Classroom classroom = classroomService.getOwnedClassroom(teacherId, classroomId);
        validateHasCorrectOption(request);
        if (missionRepository.existsByClassroomAndMissionDate(classroom, request.missionDate())) {
            throw new BusinessException(ErrorCode.DUPLICATE_MISSION_DATE);
        }
        ExpressionMission mission = new ExpressionMission(classroom, request.missionDate(), request.situation());
        mission.replaceOptions(toOptions(request.options()));
        return MissionResponse.from(missionRepository.save(mission));
    }

    public List<MissionResponse> findByClassroom(Long teacherId, Long classroomId) {
        Classroom classroom = classroomService.getOwnedClassroom(teacherId, classroomId);
        return missionRepository.findAllByClassroomOrderByMissionDateDesc(classroom).stream()
                .map(MissionResponse::from)
                .toList();
    }

    @Transactional
    public MissionResponse update(Long teacherId, Long missionId, MissionRequest request) {
        ExpressionMission mission = getOwnedMission(teacherId, missionId);
        validateHasCorrectOption(request);
        boolean dateChanged = !mission.getMissionDate().equals(request.missionDate());
        if (dateChanged && missionRepository.existsByClassroomAndMissionDate(mission.getClassroom(), request.missionDate())) {
            throw new BusinessException(ErrorCode.DUPLICATE_MISSION_DATE);
        }
        mission.update(request.missionDate(), request.situation());
        mission.replaceOptions(toOptions(request.options()));
        missionRepository.flush();
        return MissionResponse.from(mission);
    }

    @Transactional
    public void delete(Long teacherId, Long missionId) {
        missionRepository.delete(getOwnedMission(teacherId, missionId));
    }

    /** 반의 오늘 미션 (없으면 빈 값) */
    public Optional<ExpressionMission> findToday(Classroom classroom) {
        return missionRepository.findByClassroomAndMissionDate(classroom, today());
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    private ExpressionMission getOwnedMission(Long teacherId, Long missionId) {
        return missionRepository.findById(missionId)
                .filter(m -> m.getClassroom().isOwnedBy(teacherId))
                .orElseThrow(() -> new BusinessException(ErrorCode.MISSION_NOT_FOUND));
    }

    private void validateHasCorrectOption(MissionRequest request) {
        if (request.options().stream().noneMatch(OptionRequest::correct)) {
            throw new BusinessException(ErrorCode.MISSION_NEEDS_CORRECT_OPTION);
        }
    }

    private List<ExpressionOption> toOptions(List<OptionRequest> options) {
        return options.stream()
                .map(o -> new ExpressionOption(o.expression().trim(), o.correct()))
                .toList();
    }
}
