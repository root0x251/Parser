package com.example.demo.repository.tour;

import com.example.demo.entity.tour.TourPriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPriseHistoryRepository extends JpaRepository<TourPriceHistoryEntity, Long> {

    // Получение информации за последние 7 действий
    @Query(value = "SELECT * FROM public.tour_price_history WHERE tour_id = :tourID ORDER BY id DESC LIMIT 7", nativeQuery = true)
    List<TourPriceHistoryEntity> findLastSevenPrises(int tourID);

    // Цены за весь период
    @Query(value = "SELECT * FROM public.tour_price_history where tour_id = :tourID ORDER BY id ASC ", nativeQuery = true)
    List<TourPriceHistoryEntity> findAll(int tourID);

}
