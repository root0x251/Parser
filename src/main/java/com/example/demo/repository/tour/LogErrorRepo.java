package com.example.demo.repository.tour;

import com.example.demo.entity.tour.LogErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogErrorRepo extends JpaRepository<LogErrorEntity, Long> {
}

