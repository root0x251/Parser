package com.bortn.demo.repository;

import com.bortn.demo.entity.tour.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {

    @Query("SELECT COUNT(*) > 0 FROM LinkEntity l WHERE l.link = :link")
    boolean existsByLink(String link);

    @Query(value = "SELECT * FROM public.link l WHERE l.link = :link", nativeQuery = true)
    LinkEntity searchByURL(String link);

    @Query(value = "SELECT * FROM public.link WHERE is_archive = false", nativeQuery = true)
    List<LinkEntity> searchByNonArchivedLink();


}
