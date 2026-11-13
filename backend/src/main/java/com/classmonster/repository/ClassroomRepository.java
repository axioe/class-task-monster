package com.classmonster.repository;

import com.classmonster.domain.Classroom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findAllByTeacherIdOrderByIdAsc(Long teacherId);

    /**
     * 여러 학생이 동시에 먹이를 줘도 성장 에너지가 누락되지 않도록 행 잠금으로 조회한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Classroom c where c.id = :id")
    Optional<Classroom> findByIdForUpdate(@Param("id") Long id);
}
