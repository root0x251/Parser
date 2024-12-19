package com.example.demo.entity.tour;

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

    public LogErrorEntity(String description, String date, String tourName) {
        this.description = description;
        this.date = date;
        this.tourName = tourName;
    }
}
