package com.example.demo.repository.tour;

import com.example.demo.entity.tour.SelectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectorRepo extends JpaRepository<SelectorEntity, Long> {


    @Query(value = "SELECT * FROM public.selector WHERE which_site = :whichSite", nativeQuery = true)
    SelectorEntity findByWhichSite(String whichSite);

    @Query(value = "SELECT * FROM public.selector where is_template = true ORDER BY id ASC ", nativeQuery = true)
    List<SelectorEntity> findByNoTemplate();


}
