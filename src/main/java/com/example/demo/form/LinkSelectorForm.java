package com.example.demo.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkSelectorForm {
    private String url;
    private Long existingWhichSiteId;
    private String whichSite;
    private String hotelNameSelector;
    private String priceSelector;
    private String tourStartDate;
    private String hotelAddress;
}
