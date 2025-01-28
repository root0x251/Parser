package com.example.demo.entity.tour;

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
    @Column(name = "start_price")
    private int tourStartPrice;
    @Column(name = "current_price")
    private int currentPrice;
    @Column(name = "price_change")
    private String priceChange;
    @Column(name = "hotel_address", columnDefinition = "TEXT")
    private String hotelAddress;
    @Column(name = "tour_start_date", columnDefinition = "TEXT")
    private String tourStartDate;
    @Column(name = "tour_night_count")
    private int tourNightCount;
    @Column(name = "logo", columnDefinition = "TEXT")
    private String logo;
    @ElementCollection
    private List<String> images;


    @OneToOne
    @JoinColumn(name = "link_id", nullable = false)
    @JsonIgnore
    private LinkEntity link;

    @OneToMany(mappedBy = "tourEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TourPriceHistoryEntity> priceHistory = new ArrayList<>();

    public TourEntity(String hotelName, int tourStartPrice, int currentPrice, String priceChange, String hotelAddress, String tourStartDate, int tourNightCount, LinkEntity link, String logo, List<String> images) {
        this.hotelName = hotelName;
        this.tourStartPrice = tourStartPrice;
        this.currentPrice = currentPrice;
        this.priceChange = priceChange;
        this.hotelAddress = hotelAddress;
        this.tourStartDate = tourStartDate;
        this.tourNightCount = tourNightCount;
        this.link = link;
        this.logo = logo;
        this.images = images;
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
        if (priceChange.equalsIgnoreCase("Цена увеличилась")) {
            return "#ED1C24";
        } else if (priceChange.equalsIgnoreCase("Цена уменьшилась")) {
            return "#007365";
        } else {
            return "#7496a9"; // дефолт
        }
    }

}
