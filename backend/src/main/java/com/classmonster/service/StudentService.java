package com.classmonster.service;

import com.classmonster.domain.AssignmentStatus;
import com.classmonster.domain.Classroom;
import com.classmonster.domain.Role;
import com.classmonster.domain.User;
import com.classmonster.dto.student.StudentDtos.StudentCreateRequest;
import com.classmonster.dto.student.StudentDtos.StudentResponse;
import com.classmonster.dto.student.StudentDtos.StudentUpdateRequest;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.AssignmentRepository;
import com.classmonster.repository.FeedingLogRepository;
import com.classmonster.repository.FoodItemRepository;
import com.classmonster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 학생 계정 관리(교사용)와 학생 조회.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final FoodItemRepository foodItemRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final ClassroomService classroomService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentResponse create(Long teacherId, Long classroomId, StudentCreateRequest request) {
        Classroom classroom = classroomService.getOwnedClassroom(teacherId, classroomId);
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        User student = User.student(request.loginId(), passwordEncoder.encode(request.password()),
                request.nickname(), classroom);
        return toResponse(userRepository.save(student));
    }

    public List<StudentResponse> findAll(Long teacherId, Long classroomId) {
        Classroom classroom = classroomService.getOwnedClassroom(teacherId, classroomId);
        return userRepository.findAllByClassroomAndRoleOrderByNicknameAsc(classroom, Role.STUDENT).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StudentResponse update(Long teacherId, Long studentId, StudentUpdateRequest request) {
        User student = getOwnedStudent(teacherId, studentId);
        student.changeNickname(request.nickname());
        if (StringUtils.hasText(request.password())) {
            student.changePassword(passwordEncoder.encode(request.password()));
        }
        return toResponse(student);
    }

    /** 학생 계정과 학생의 과제·먹이·기록을 함께 삭제한다. */
    @Transactional
    public void delete(Long teacherId, Long studentId) {
        User student = getOwnedStudent(teacherId, studentId);
        feedingLogRepository.deleteAllByStudent(student);
        foodItemRepository.deleteAllByOwner(student);
        assignmentRepository.deleteAllByStudent(student);
        userRepository.deleteById(student.getId());
    }

    /** 로그인한 교사의 반 학생인지 확인하고 돌려준다. */
    public User getOwnedStudent(Long teacherId, Long studentId) {
        User student = getStudent(studentId);
        if (!student.getClassroom().isOwnedBy(teacherId)) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }
        return student;
    }

    public User getStudent(Long studentId) {
        return userRepository.findById(studentId)
                .filter(User::isStudent)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
    }

    private StudentResponse toResponse(User student) {
        return StudentResponse.of(student,
                foodItemRepository.countByOwnerAndConsumedFalse(student),
                assignmentRepository.countByStudentAndStatus(student, AssignmentStatus.APPROVED),
                feedingLogRepository.countByStudent(student),
                feedingLogRepository.countByStudentAndSuccessTrue(student));
    }
}
