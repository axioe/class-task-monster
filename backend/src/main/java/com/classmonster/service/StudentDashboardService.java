package com.classmonster.service;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.User;
import com.classmonster.dto.dashboard.StudentDashboardResponse;
import com.classmonster.dto.dashboard.StudentGrowthResponse;
import com.classmonster.dto.feeding.FeedingDtos.FeedingLogResponse;
import com.classmonster.dto.mission.MissionDtos.StudentMissionResponse;
import com.classmonster.dto.monster.MonsterResponse;
import com.classmonster.repository.FeedingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 학생 화면용 조회를 한 곳에서 조합한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentDashboardService {

    private final StudentService studentService;
    private final AssignmentService assignmentService;
    private final FoodService foodService;
    private final MissionService missionService;
    private final GrowthService growthService;
    private final FeedingLogRepository feedingLogRepository;

    /** 학생 메인 화면 */
    public StudentDashboardResponse getDashboard(Long studentId) {
        User student = studentService.getStudent(studentId);
        Classroom classroom = student.getClassroom();

        StudentMissionResponse todayMission = missionService.findToday(classroom)
                .map(StudentMissionResponse::from)
                .orElse(null);

        return new StudentDashboardResponse(
                student.getNickname(),
                classroom.getId(),
                classroom.getName(),
                MonsterResponse.from(classroom),
                assignmentService.findMine(student),
                foodService.findAvailable(student),
                todayMission,
                growthService.recentNews(classroom));
    }

    /** 성장 기록 화면 */
    public StudentGrowthResponse getGrowth(Long studentId) {
        User student = studentService.getStudent(studentId);
        Classroom classroom = student.getClassroom();
        return new StudentGrowthResponse(
                MonsterResponse.from(classroom),
                growthService.allNews(classroom),
                feedingLogRepository.findTop20ByStudentOrderByIdDesc(student).stream()
                        .map(FeedingLogResponse::from)
                        .toList(),
                feedingLogRepository.countByStudentAndSuccessTrue(student));
    }
}
