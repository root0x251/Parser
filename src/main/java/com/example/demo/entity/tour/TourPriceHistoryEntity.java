package com.example.demo.entity.tour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tour_price_history")
public class TourPriceHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "old_price" ,nullable = false)
    private int oldPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    @JsonIgnore
    private TourEntity tourEntity;

    public TourPriceHistoryEntity(String date, int oldPrice, TourEntity tourEntity) {
        this.date = date;
        this.oldPrice = oldPrice;
        this.tourEntity = tourEntity;
    }


    @Override
    public String toString() {
        return "TourPriceHistoryEntity{" +
                "date=" + date +
                ", oldPrice=" + oldPrice +
                '}';
    }
}
