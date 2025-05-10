package com.bortn.demo.entity.tour;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "error_core_log")
public class LogErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    private String date;
    @Column(name = "tour_name")
    private String tourName;
    @Column(name = "tour_link", columnDefinition = "TEXT")
    private String tourLink;

    public LogErrorEntity(String description, String date) {
        this.description = description;
        this.date = date;
    }

    public LogErrorEntity(String description, String date, String tourName, String tourLink) {
        this.description = description;
        this.date = date;
        this.tourName = tourName;
        this.tourLink = tourLink;
    }
}
