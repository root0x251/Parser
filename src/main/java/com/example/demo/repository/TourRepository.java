package com.example.demo.repository;

import com.example.demo.entity.LinkEntity;
import com.example.demo.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {
    Optional<TourEntity> findByLink(LinkEntity link);


}
