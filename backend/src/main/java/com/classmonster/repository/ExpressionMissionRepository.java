package com.classmonster.repository;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.ExpressionMission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpressionMissionRepository extends JpaRepository<ExpressionMission, Long> {

    @EntityGraph(attributePaths = "options")
    Optional<ExpressionMission> findByClassroomAndMissionDate(Classroom classroom, LocalDate missionDate);

    boolean existsByClassroomAndMissionDate(Classroom classroom, LocalDate missionDate);

    List<ExpressionMission> findAllByClassroomOrderByMissionDateDesc(Classroom classroom);

    /** 옵션까지 cascade 로 지우기 위해 엔티티 단위로 삭제한다. */
    List<ExpressionMission> findAllByClassroom(Classroom classroom);
}
