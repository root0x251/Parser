package com.example.demo.repository;

import com.example.demo.entity.SelectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectorRepo extends JpaRepository<SelectorEntity, Long> {


    @Query(value = "SELECT * FROM public.selector WHERE which_site = :whichSite", nativeQuery = true)
    SelectorEntity findByWhichSite(String whichSite);


}
