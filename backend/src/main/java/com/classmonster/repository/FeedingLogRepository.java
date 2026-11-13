package com.classmonster.repository;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.FeedingLog;
import com.classmonster.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedingLogRepository extends JpaRepository<FeedingLog, Long> {

    List<FeedingLog> findTop20ByStudentOrderByIdDesc(User student);

    long countByStudentAndSuccessTrue(User student);

    long countByStudent(User student);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from FeedingLog f where f.classroom = :classroom")
    void deleteAllByClassroom(@Param("classroom") Classroom classroom);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from FeedingLog f where f.student = :student")
    void deleteAllByStudent(@Param("student") User student);
}
