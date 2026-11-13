package com.classmonster.repository;

import com.classmonster.domain.Classroom;
import com.classmonster.domain.FoodItem;
import com.classmonster.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findAllByOwnerAndConsumedFalseOrderByIdAsc(User owner);

    Optional<FoodItem> findFirstByOwnerAndConsumedFalseOrderByIdAsc(User owner);

    long countByOwnerAndConsumedFalse(User owner);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from FoodItem f where f.owner.id in (select u.id from User u where u.classroom = :classroom)")
    void deleteAllByClassroom(@Param("classroom") Classroom classroom);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from FoodItem f where f.owner = :owner")
    void deleteAllByOwner(@Param("owner") User owner);
}
