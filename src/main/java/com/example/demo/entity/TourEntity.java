package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tour")
public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "hotel_name")
    private String hotelName;
    @Column(name = "current_price")
    private int currentPrice;
    @Column(name = "price_change")
    private String priceChange;

    @OneToOne
    @JoinColumn(name = "link_id", nullable = false)
    @JsonIgnore
    private LinkEntity link;

    @OneToMany(mappedBy = "tourEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TourPriceHistoryEntity> priceHistory = new ArrayList<>();

    public TourEntity(String hotelName, int currentPrice, String priceChange, LinkEntity link) {
        this.hotelName = hotelName;
        this.currentPrice = currentPrice;
        this.priceChange = priceChange;
        this.link = link;
    }

    @Override
    public String toString() {
        return "TourEntity{" +
                "\nid=" + id +
                ", \nhotelName='" + hotelName + '\'' +
                ", \ncurrentPrice=" + currentPrice +
                ", \npriceChange='" + priceChange + '\'' +
                ", \nlink=" + link.getLink() +
                ", \npriceHistory=" + priceHistory +
                '}' + "\n";
    }

    public String getPriceColor() {
        if (priceChange.equals("цена увеличилась")) {
            return "#ED1C24"; // увеличение цены
        } else if (priceChange.equals("цена уменьшилась")) {
            return "#007365"; // уменьшение цены
        } else {
            return "#7496a9"; // дефолт
        }
    }

}
