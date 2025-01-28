package com.bortn.demo.repository;

import com.bortn.demo.entity.tour.LinkEntity;
import com.bortn.demo.entity.tour.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {
    Optional<TourEntity> findByLink(LinkEntity link);


}
