package com.classmonster.repository;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.Role;
import com.classmonster.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    List<User> findAllByClassroomAndRoleOrderByNicknameAsc(Classroom classroom, Role role);

    long countByClassroomAndRole(Classroom classroom, Role role);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from User u where u.classroom = :classroom")
    void deleteAllByClassroom(@Param("classroom") Classroom classroom);
}
