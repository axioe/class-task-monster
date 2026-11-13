package com.classmonster.service;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.ExpressionMission;
import com.classmonster.domain.Role;
import com.classmonster.domain.User;
import com.classmonster.dto.classroom.ClassroomDtos.ClassroomRequest;
import com.classmonster.dto.classroom.ClassroomDtos.ClassroomResponse;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.AssignmentRepository;
import com.classmonster.repository.ClassroomRepository;
import com.classmonster.repository.ExpressionMissionRepository;
import com.classmonster.repository.FeedingLogRepository;
import com.classmonster.repository.FoodItemRepository;
import com.classmonster.repository.GrowthNewsRepository;
import com.classmonster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 교사의 반 관리 (생성·조회·수정·삭제).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final FoodItemRepository foodItemRepository;
    private final ExpressionMissionRepository missionRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final GrowthNewsRepository growthNewsRepository;
    private final GrowthService growthService;

    @Transactional
    public ClassroomResponse create(Long teacherId, ClassroomRequest request) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Classroom classroom = classroomRepository.save(new Classroom(request.name(), request.monsterName(), teacher));
        growthService.announceArrival(classroom);
        return ClassroomResponse.of(classroom, 0);
    }

    public List<ClassroomResponse> findMine(Long teacherId) {
        return classroomRepository.findAllByTeacherIdOrderByIdAsc(teacherId).stream()
                .map(c -> ClassroomResponse.of(c, countStudents(c)))
                .toList();
    }

    public ClassroomResponse findOne(Long teacherId, Long classroomId) {
        Classroom classroom = getOwnedClassroom(teacherId, classroomId);
        return ClassroomResponse.of(classroom, countStudents(classroom));
    }

    @Transactional
    public ClassroomResponse update(Long teacherId, Long classroomId, ClassroomRequest request) {
        Classroom classroom = getOwnedClassroom(teacherId, classroomId);
        classroom.update(request.name(), request.monsterName());
        return ClassroomResponse.of(classroom, countStudents(classroom));
    }

    /**
     * 반과 반에 속한 모든 데이터(학생 계정, 과제, 먹이, 미션, 기록, 소식)를 함께 삭제한다.
     * 외래키 순서대로 자식 데이터부터 지운다.
     */
    @Transactional
    public void delete(Long teacherId, Long classroomId) {
        Classroom classroom = getOwnedClassroom(teacherId, classroomId);

        feedingLogRepository.deleteAllByClassroom(classroom);
        foodItemRepository.deleteAllByClassroom(classroom);
        assignmentRepository.deleteAllByClassroom(classroom);
        List<ExpressionMission> missions = missionRepository.findAllByClassroom(classroom);
        missionRepository.deleteAll(missions);
        missionRepository.flush();
        growthNewsRepository.deleteAllByClassroom(classroom);
        userRepository.deleteAllByClassroom(classroom);
        classroomRepository.deleteById(classroom.getId());
    }

    /**
     * 로그인한 교사의 반인지 확인하고 돌려준다. 다른 서비스에서도 접근 제어에 사용한다.
     */
    public Classroom getOwnedClassroom(Long teacherId, Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));
        if (!classroom.isOwnedBy(teacherId)) {
            // 다른 교사의 반 존재 여부를 노출하지 않도록 NOT_FOUND 로 응답
            throw new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        return classroom;
    }

    private long countStudents(Classroom classroom) {
        return userRepository.countByClassroomAndRole(classroom, Role.STUDENT);
    }
}
