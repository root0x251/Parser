package com.example.demo.repository.tour;

import com.example.demo.entity.tour.ParserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParserInfoRepository extends JpaRepository<ParserInfoEntity, Long> {

    @Query(value = "SELECT * FROM parser_info ORDER BY id DESC LIMIT 1", nativeQuery = true)
    ParserInfoEntity findLast();

}
