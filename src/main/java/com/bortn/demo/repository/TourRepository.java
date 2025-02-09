package com.bortn.demo.repository;

import com.bortn.demo.entity.tour.LinkEntity;
import com.bortn.demo.entity.tour.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {
    Optional<TourEntity> findByLink(LinkEntity link);

    @Query(value = "SELECT tour.* FROM tour tour JOIN link link ON tour.link_id = link.id WHERE link.is_archive = :value ORDER BY tour.current_price ASC", nativeQuery = true)
    List<TourEntity> searchByArchivedLink(boolean value);

    @Query(value = "SELECT * FROM tour WHERE link_id = :linkId", nativeQuery = true)
    Optional <TourEntity> searchByLinkId(Long linkId);




}
