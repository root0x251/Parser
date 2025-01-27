package com.example.demo.repository.tour;

import com.example.demo.entity.tour.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {
    @Query("SELECT COUNT(*) > 0 FROM LinkEntity l WHERE l.link = :link")
    boolean existsByLink(String link);

    @Query(value = "SELECT * FROM public.link WHERE is_Archive = false", nativeQuery = true)
    List<LinkEntity> findNoArch();
}
