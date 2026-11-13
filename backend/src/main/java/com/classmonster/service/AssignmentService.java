package com.classmonster.service;

import com.classmonster.domain.Assignment;
import com.classmonster.domain.AssignmentStatus;
import com.classmonster.domain.Classroom;
import com.classmonster.domain.FoodItem;
import com.classmonster.domain.User;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentCreateRequest;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentResponse;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentReviewRequest;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentReviewResponse;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentSubmitRequest;
import com.classmonster.dto.assignment.AssignmentDtos.AssignmentUpdateRequest;
import com.classmonster.dto.food.FoodResponse;
import com.classmonster.exception.BusinessException;
import com.classmonster.exception.ErrorCode;
import com.classmonster.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 맞춤 과제: 교사의 지정·확인(피드백)과 학생의 확인 요청.
 * 교사가 확인(승인)하면 과제당 1회 먹이를 지급한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ClassroomService classroomService;
    private final StudentService studentService;
    private final FoodService foodService;
    private final Clock clock;

    // ===== 교사 =====

    @Transactional
    public AssignmentResponse create(Long teacherId, Long classroomId, AssignmentCreateRequest request) {
        Classroom classroom = classroomService.getOwnedClassroom(teacherId, classroomId);
        User student = studentService.getOwnedStudent(teacherId, request.studentId());
        if (!student.getClassroom().getId().equals(classroom.getId())) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }
        Assignment assignment = new Assignment(classroom, student, request.title(), request.description());
        return AssignmentResponse.from(assignmentRepository.save(assignment));
    }

    /** status 가 null 이면 반 전체 과제를 조회한다. */
    public List<AssignmentResponse> findByClassroom(Long teacherId, Long classroomId, AssignmentStatus status) {
        Classroom classroom = classroomService.getOwnedClassroom(teacherId, classroomId);
        List<Assignment> assignments = status == null
                ? assignmentRepository.findAllByClassroomOrderByIdDesc(classroom)
                : assignmentRepository.findAllByClassroomAndStatusOrderByIdDesc(classroom, status);
        return assignments.stream().map(AssignmentResponse::from).toList();
    }

    @Transactional
    public AssignmentResponse update(Long teacherId, Long assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = getOwnedAssignment(teacherId, assignmentId);
        assignment.updateContent(request.title(), request.description());
        return AssignmentResponse.from(assignment);
    }

    @Transactional
    public void delete(Long teacherId, Long assignmentId) {
        assignmentRepository.delete(getOwnedAssignment(teacherId, assignmentId));
    }

    /**
     * 수행 확인. 승인 시 먹이 1개를 무작위 종류로 지급한다 (과제당 1회).
     */
    @Transactional
    public AssignmentReviewResponse review(Long teacherId, Long assignmentId, AssignmentReviewRequest request) {
        Assignment assignment = getOwnedAssignment(teacherId, assignmentId);
        if (!assignment.canReview()) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_REVIEWABLE);
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (!request.approved()) {
            assignment.requestRevision(request.comment(), now);
            return new AssignmentReviewResponse(AssignmentResponse.from(assignment), null);
        }

        assignment.approve(request.comment(), now);
        FoodResponse grantedFood = null;
        if (!assignment.isFoodGranted()) {
            FoodItem food = foodService.grantRandomFood(assignment.getStudent());
            assignment.markFoodGranted();
            grantedFood = FoodResponse.from(food);
        }
        return new AssignmentReviewResponse(AssignmentResponse.from(assignment), grantedFood);
    }

    // ===== 학생 =====

    public List<AssignmentResponse> findMine(User student) {
        return assignmentRepository.findAllByStudentOrderByIdDesc(student).stream()
                .map(AssignmentResponse::from)
                .toList();
    }

    /** 학생이 과제를 마치고 선생님께 확인을 요청한다. */
    @Transactional
    public AssignmentResponse submit(Long studentId, Long assignmentId, AssignmentSubmitRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .filter(a -> a.isAssignedTo(studentId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if (!assignment.canSubmit()) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_SUBMITTABLE);
        }
        assignment.submit(request == null ? null : request.note(), LocalDateTime.now(clock));
        return AssignmentResponse.from(assignment);
    }

    private Assignment getOwnedAssignment(Long teacherId, Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .filter(a -> a.getClassroom().isOwnedBy(teacherId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
    }
}
