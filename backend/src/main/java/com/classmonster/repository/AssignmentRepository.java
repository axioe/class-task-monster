package com.classmonster.repository;

import com.classmonster.domain.Assignment;
import com.classmonster.domain.AssignmentStatus;
import com.classmonster.domain.Classroom;
import com.classmonster.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByStudentOrderByIdDesc(User student);

    long countByStudentAndStatus(User student, AssignmentStatus status);

    @EntityGraph(attributePaths = "student")
    List<Assignment> findAllByClassroomOrderByIdDesc(Classroom classroom);

    @EntityGraph(attributePaths = "student")
    List<Assignment> findAllByClassroomAndStatusOrderByIdDesc(Classroom classroom, AssignmentStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Assignment a where a.classroom = :classroom")
    void deleteAllByClassroom(@Param("classroom") Classroom classroom);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Assignment a where a.student = :student")
    void deleteAllByStudent(@Param("student") User student);
}
