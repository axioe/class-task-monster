package com.classmonster.repository;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.GrowthNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrowthNewsRepository extends JpaRepository<GrowthNews, Long> {

    List<GrowthNews> findTop5ByClassroomOrderByIdDesc(Classroom classroom);

    List<GrowthNews> findAllByClassroomOrderByIdDesc(Classroom classroom);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from GrowthNews n where n.classroom = :classroom")
    void deleteAllByClassroom(@Param("classroom") Classroom classroom);
}
