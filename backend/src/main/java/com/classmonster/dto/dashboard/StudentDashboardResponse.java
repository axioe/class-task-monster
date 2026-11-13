package com.classmonster.dto.dashboard;

import com.classmonster.dto.assignment.AssignmentDtos.AssignmentResponse;
import com.classmonster.dto.food.FoodResponse;
import com.classmonster.dto.mission.MissionDtos.StudentMissionResponse;
import com.classmonster.dto.monster.MonsterResponse;
import com.classmonster.dto.news.GrowthNewsResponse;

import java.util.List;

/**
 * 학생 메인 화면 한 번에 그리기 위한 응답.
 * 상단(반 이름·별명) / 중앙(몬스터) / 오른쪽 위(오늘 도전) / 오른쪽 아래(먹이·표현) / 하단(성장 소식)
 *
 * @param todayMission 오늘의 표현 미션 (교사가 아직 등록하지 않았으면 null)
 */
public record StudentDashboardResponse(String nickname, Long classroomId, String classroomName,
                                       MonsterResponse monster, List<AssignmentResponse> assignments,
                                       List<FoodResponse> foods, StudentMissionResponse todayMission,
                                       List<GrowthNewsResponse> news) {
}
