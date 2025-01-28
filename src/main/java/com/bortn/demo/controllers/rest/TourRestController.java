package com.bortn.demo.controllers.rest;

import com.bortn.demo.entity.tour.LinkEntity;
import com.bortn.demo.entity.tour.SelectorEntity;
import com.bortn.demo.entity.tour.TourEntity;
import com.bortn.demo.entity.tour.TourPriceHistoryEntity;
import com.bortn.demo.repository.LinkRepository;
import com.bortn.demo.repository.SelectorRepo;
import com.bortn.demo.repository.TourPriseHistoryRepository;
import com.bortn.demo.repository.TourRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tour")
public class TourRestController {

    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;
    private final LinkRepository linkRepository;
    private final SelectorRepo selectorRepository;

    public TourRestController(TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository, LinkRepository linkRepository, SelectorRepo selectorRepository) {
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
        this.linkRepository = linkRepository;
        this.selectorRepository = selectorRepository;
    }

    @GetMapping("/allTour")
    public List<TourEntity> allTour() {
        return tourRepository.findAll();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTourAndRelatedData(@PathVariable Long id) {
        // Поиск тура по ID
        Optional<TourEntity> optionalTour = tourRepository.findById(id);

        if (optionalTour.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // поиск всего и вся
        TourEntity tour = optionalTour.get();
        List<TourPriceHistoryEntity> tourPriceHistory = tour.getPriceHistory();
        LinkEntity link = tour.getLink();  // Получаем связанную запись Link
        SelectorEntity selector = link.getSelectorEntity();  // Получаем связанную запись Selector


        // Удаление
        tourPriseHistoryRepository.deleteAll(tourPriceHistory);
        tourRepository.delete(tour);
        linkRepository.delete(link);

        // проверка, пустой ли which_site у селектора (у нас могут остаться селекторы для парсинга других сайтов)
        if (selector.getWhichSite() == null || selector.getWhichSite().isEmpty()) {
            selectorRepository.delete(selector);
        }

        return ResponseEntity.ok("Tour and related data successfully deleted");
    }


}
