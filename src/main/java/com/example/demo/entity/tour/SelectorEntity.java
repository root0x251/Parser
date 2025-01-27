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
    @Column(name = "id")
    private Long id;

    @Column(name = "hotel_name_selector", columnDefinition = "TEXT")
    private String hotelSelector;

    @Column(name = "price_selector", columnDefinition = "TEXT")
    private String priceSelector;

    @Column(name = "which_site", columnDefinition = "TEXT")
    private String whichSite;

    @Column(name = "tour_start_date", columnDefinition = "TEXT")
    private String tourStartDateSelector;

    @Column(name = "hotel_address", columnDefinition = "TEXT")
    private String hotelAddressSelector;

    @Column(name = "site_logo", columnDefinition = "TEXT")
    private String siteLogo;

    @Column(name = "is_template")
    private boolean isTemplate;

    public SelectorEntity(String hotelSelector, String priceSelector, String whichSite, String tourStartDateSelector, String hotelAddressSelector, String siteLogo, boolean isTemplate) {
        this.hotelSelector = hotelSelector;
        this.priceSelector = priceSelector;
        this.whichSite = whichSite;
        this.tourStartDateSelector = tourStartDateSelector;
        this.hotelAddressSelector = hotelAddressSelector;
        this.siteLogo = siteLogo;
        this.isTemplate = isTemplate;
    }

}
