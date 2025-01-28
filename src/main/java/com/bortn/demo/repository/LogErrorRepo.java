package com.bortn.demo.repository;

import com.bortn.demo.entity.tour.LogErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogErrorRepo extends JpaRepository<LogErrorEntity, Long> {

    @Query(value = "SELECT COUNT(*) > 0 FROM error_core_log WHERE date = :date", nativeQuery = true)
    boolean existsByDate(@Param("date") String date);

    @Query(value = "SELECT * FROM error_core_log ORDER BY tour_name ASC", nativeQuery = true)
    List<LogErrorEntity> findAllSortedByTourName();

}

