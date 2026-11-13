package com.classmonster.dto.mission;

import com.classmonster.domain.ExpressionMission;
import com.classmonster.domain.ExpressionOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public final class MissionDtos {

    private MissionDtos() {
    }

    /** 교사의 표현 미션 생성/수정 */
    public record MissionRequest(
            @NotNull LocalDate missionDate,
            @NotBlank @Size(max = 200) String situation,
            @NotNull @Size(min = 2, max = 6, message = "표현은 2개 이상 6개 이하로 입력해 주세요.")
            List<@Valid OptionRequest> options) {
    }

    public record OptionRequest(
            @NotBlank @Size(max = 100) String expression,
            boolean correct) {
    }

    /** 교사용 응답: 정답 여부 포함 */
    public record MissionResponse(Long id, LocalDate missionDate, String situation, List<OptionResponse> options) {
        public static MissionResponse from(ExpressionMission mission) {
            return new MissionResponse(mission.getId(), mission.getMissionDate(), mission.getSituation(),
                    mission.getOptions().stream().map(OptionResponse::from).toList());
        }
    }

    public record OptionResponse(Long id, String expression, boolean correct) {
        public static OptionResponse from(ExpressionOption option) {
            return new OptionResponse(option.getId(), option.getExpression(), option.isCorrect());
        }
    }

    /** 학생용 응답: 정답 여부를 숨긴다. */
    public record StudentMissionResponse(Long id, LocalDate missionDate, String situation,
                                         List<StudentOptionResponse> options) {
        public static StudentMissionResponse from(ExpressionMission mission) {
            return new StudentMissionResponse(mission.getId(), mission.getMissionDate(), mission.getSituation(),
                    mission.getOptions().stream().map(StudentOptionResponse::from).toList());
        }
    }

    public record StudentOptionResponse(Long id, String expression) {
        public static StudentOptionResponse from(ExpressionOption option) {
            return new StudentOptionResponse(option.getId(), option.getExpression());
        }
    }
}
