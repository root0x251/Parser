package com.example.demo.entity.tour;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "selector")
public class SelectorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hotel_name_selector", columnDefinition = "TEXT", nullable = false)
    private String hotelSelector;

    @Column(name = "price_selector", columnDefinition = "TEXT", nullable = false)
    private String priceSelector;

    @Column(name = "which_site", columnDefinition = "TEXT")
    private String whichSite;

    public SelectorEntity(String hotelSelector, String priceSelector) {
        this.hotelSelector = hotelSelector;
        this.priceSelector = priceSelector;
    }

    public SelectorEntity(String hotelSelector, String priceSelector, String whichSite) {
        this.hotelSelector = hotelSelector;
        this.priceSelector = priceSelector;
        this.whichSite = whichSite;
    }
}
