package com.classmonster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 파일럿 범위 전체 흐름: 과제 카드 → 교사 확인 → 먹이 획득 → (표현 미션 성공 시) 몬스터 성장
 */
@SpringBootTest
@AutoConfigureMockMvc
class PilotFlowIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("과제 확인으로 먹이를 받고, 알맞은 표현을 골랐을 때만 몬스터가 성장한다")
    void pilotFlow() throws Exception {
        // 교사 가입 → 로그인 → 반 생성
        perform(post("/api/auth/signup"), null, Map.of(
                "loginId", "flow_teacher", "password", "password123", "nickname", "박선생"))
                .andExpect(status().isCreated());
        String teacherToken = login("flow_teacher", "password123");

        JsonNode classroom = read(perform(post("/api/teacher/classrooms"), teacherToken,
                Map.of("name", "3학년 1반", "monsterName", "뭉치"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monster.stage").value("EGG")));
        long classroomId = classroom.get("id").asLong();

        // 학생 계정 생성
        JsonNode student = read(perform(post("/api/teacher/classrooms/" + classroomId + "/students"), teacherToken,
                Map.of("loginId", "flow_student", "password", "pass1234", "nickname", "하늘"))
                .andExpect(status().isCreated()));
        long studentId = student.get("id").asLong();

        // 과제 지정
        JsonNode assignment = read(perform(post("/api/teacher/classrooms/" + classroomId + "/assignments"), teacherToken,
                Map.of("studentId", studentId, "title", "단어 3개 익히기", "description", "cat, dog, bird"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS")));
        long assignmentId = assignment.get("id").asLong();

        // 오늘의 표현 미션 등록 (정답 1개)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        JsonNode mission = read(perform(post("/api/teacher/classrooms/" + classroomId + "/missions"), teacherToken,
                Map.of("missionDate", today.toString(), "situation", "뭉치에게 먹이를 건네줘",
                        "options", List.of(
                                Map.of("expression", "Here you are!", "correct", true),
                                Map.of("expression", "Good night!", "correct", false))))
                .andExpect(status().isCreated()));
        long correctOptionId = mission.get("options").get(0).get("id").asLong();
        long wrongOptionId = mission.get("options").get(1).get("id").asLong();

        // 학생 로그인 → 교사 API 접근 불가
        String studentToken = login("flow_student", "pass1234");
        perform(get("/api/teacher/classrooms"), studentToken, null).andExpect(status().isForbidden());

        // 학생 대시보드: 정답 여부는 노출되지 않는다
        perform(get("/api/student/dashboard"), studentToken, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classroomName").value("3학년 1반"))
                .andExpect(jsonPath("$.todayMission.options[0].correct").doesNotExist())
                .andExpect(jsonPath("$.foods.length()").value(0));

        // 먹이 없이 먹이 주기 → 실패
        perform(post("/api/student/feed"), studentToken, Map.of("optionId", correctOptionId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("NO_FOOD"));

        // 확인 요청 전에는 교사가 확인할 수 없다
        perform(post("/api/teacher/assignments/" + assignmentId + "/review"), teacherToken,
                Map.of("approved", true, "comment", "좋아요"))
                .andExpect(status().isConflict());

        // 학생 확인 요청 → 교사 확인(승인) → 먹이 1개 지급
        perform(post("/api/student/assignments/" + assignmentId + "/submit"), studentToken,
                Map.of("note", "다 외웠어요"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
        perform(post("/api/teacher/assignments/" + assignmentId + "/review"), teacherToken,
                Map.of("approved", true, "comment", "발음이 정확해요!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignment.status").value("APPROVED"))
                .andExpect(jsonPath("$.grantedFood.type").exists());

        // 잘못된 표현 → 실패, 먹이는 그대로
        perform(post("/api/student/feed"), studentToken, Map.of("optionId", wrongOptionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.remainingFoodCount").value(1))
                .andExpect(jsonPath("$.monster.energy").value(0));

        // 알맞은 표현 → 성공, 먹이 1개 사용, 에너지 +1 (1:1)
        perform(post("/api/student/feed"), studentToken, Map.of("optionId", correctOptionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.remainingFoodCount").value(0))
                .andExpect(jsonPath("$.monster.energy").value(1))
                .andExpect(jsonPath("$.monster.nextStageEnergy").value(10));

        // 교사 화면 학생 지표: 시도 2번 중 성공 1번
        perform(get("/api/teacher/classrooms/" + classroomId + "/students"), teacherToken, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].approvedAssignmentCount").value(1))
                .andExpect(jsonPath("$[0].feedingAttemptCount").value(2))
                .andExpect(jsonPath("$[0].feedingSuccessCount").value(1));

        // 반 삭제 → 학생 계정도 함께 삭제되어 로그인 불가
        perform(delete("/api/teacher/classrooms/" + classroomId), teacherToken, null)
                .andExpect(status().isNoContent());
        perform(post("/api/auth/login"), null, Map.of("loginId", "flow_student", "password", "pass1234"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("다른 교사의 반에는 접근할 수 없다")
    void cannotAccessOtherTeachersClassroom() throws Exception {
        perform(post("/api/auth/signup"), null, Map.of(
                "loginId", "owner_teacher", "password", "password123", "nickname", "주인"))
                .andExpect(status().isCreated());
        perform(post("/api/auth/signup"), null, Map.of(
                "loginId", "other_teacher", "password", "password123", "nickname", "다른"))
                .andExpect(status().isCreated());

        String ownerToken = login("owner_teacher", "password123");
        String otherToken = login("other_teacher", "password123");

        long classroomId = read(perform(post("/api/teacher/classrooms"), ownerToken,
                Map.of("name", "4학년 3반", "monsterName", "콩이"))).get("id").asLong();

        perform(get("/api/teacher/classrooms/" + classroomId), otherToken, null)
                .andExpect(status().isNotFound());
        perform(get("/api/teacher/classrooms/" + classroomId), null, null)
                .andExpect(status().isUnauthorized());
    }

    private String login(String loginId, String password) throws Exception {
        JsonNode body = read(perform(post("/api/auth/login"), null,
                Map.of("loginId", loginId, "password", password)).andExpect(status().isOk()));
        assertThat(body.get("tokenType").asText()).isEqualTo("Bearer");
        return body.get("accessToken").asText();
    }

    private ResultActions perform(MockHttpServletRequestBuilder builder, String token, Object body) throws Exception {
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            builder.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
        }
        return mockMvc.perform(builder);
    }

    private JsonNode read(ResultActions actions) throws Exception {
        return objectMapper.readTree(actions.andReturn().getResponse().getContentAsString());
    }
}
