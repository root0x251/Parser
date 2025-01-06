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

    @Column(name = "tour_start_date")
    private String tourStartDateSelector;

    @Column(name = "hotel_address")
    private String hotelAddressSelector;

    public SelectorEntity(String hotelSelector, String priceSelector, String whichSite, String tourStartDateSelector, String hotelAddressSelector) {
        this.hotelSelector = hotelSelector;
        this.priceSelector = priceSelector;
        this.whichSite = whichSite;
        this.tourStartDateSelector = tourStartDateSelector;
        this.hotelAddressSelector = hotelAddressSelector;
    }
}
