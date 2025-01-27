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
    @Column(name = "error_code", columnDefinition = "TEXT")
    private String errorCode;
    private String date;
    @Column(name = "description")
    private String description;
    @Column(name = "tour_link", columnDefinition = "TEXT")
    private String tourLink;

    public LogErrorEntity(String errorCode, String date, String description) {
        this.errorCode = errorCode;
        this.date = date;
        this.description = description;
    }

    public LogErrorEntity(String errorCode, String date, String description, String tourLink) {
        this.errorCode = errorCode;
        this.date = date;
        this.description = description;
        this.tourLink = tourLink;
    }
}
