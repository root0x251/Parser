package com.bortn.demo.entity.tour;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "parser_info")
public class ParserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_parsing_time")
    private String startParsingTime;
    @Column(name = "end_parsing_time")
    private String endParsingTime;
    @Column(name = "count_of_passes")
    private Long passesCount;
    @Column(name = "count_of_error")
    private Long errorCount;

    public ParserInfoEntity(String startParsingTime, String endParsingTime, Long passesCount, Long errorCount) {
        this.startParsingTime = startParsingTime;
        this.endParsingTime = endParsingTime;
        this.passesCount = passesCount;
        this.errorCount = errorCount;
    }
}
