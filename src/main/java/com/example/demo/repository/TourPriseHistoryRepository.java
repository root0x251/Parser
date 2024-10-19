package com.example.demo.repository;

import com.example.demo.entity.TourPriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourPriseHistoryRepository extends JpaRepository<TourPriceHistoryEntity, Long> {

}
