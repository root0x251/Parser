package com.example.demo.controllers;

import com.example.demo.entity.tour.LinkEntity;
import com.example.demo.entity.tour.SelectorEntity;
import com.example.demo.entity.tour.TourEntity;
import com.example.demo.entity.tour.TourPriceHistoryEntity;
import com.example.demo.repository.tour.LinkRepository;
import com.example.demo.repository.tour.SelectorRepo;
import com.example.demo.repository.tour.TourPriseHistoryRepository;
import com.example.demo.repository.tour.TourRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TourViewController {

    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;
    private final LinkRepository linkRepository;
    private final SelectorRepo selectorRepository;

    public TourViewController(TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository, LinkRepository linkRepository, SelectorRepo selectorRepository) {
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
        this.linkRepository = linkRepository;
        this.selectorRepository = selectorRepository;
    }

    // all tours
    @GetMapping("/tours")
    public String listAllTours(Model model) {
        model.addAttribute("tours", tourRepository.findAll());
        return "tour-list";
    }

    // подробнее
    @GetMapping("/tour/{id}")
    public String getTourDetails(@PathVariable Long id, Model model) {

        Optional<TourEntity> optionalTour = tourRepository.findById(id);
        if (optionalTour.isEmpty()) {
            return "tour-not-found"; // шаблон с ошибкой
        }

        TourEntity tour = optionalTour.get();
        LinkEntity link = tour.getLink();
        List<TourPriceHistoryEntity> tourPriceHistory = tour.getPriceHistory();

        int oldPrice = tourPriceHistory.get(tourPriceHistory.size() - 1).getOldPrice();
        int priceDifference = tour.getCurrentPrice() - oldPrice;

        // Преобразуем данные из priceHistory для использования в графике
        List<String> dates = new ArrayList<>();
        List<Integer> prices = new ArrayList<>();

        for (TourPriceHistoryEntity history : tourPriceHistory) {
            dates.add(history.getDate());
            prices.add(history.getOldPrice());
        }


        // Добавление последней цены
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        prices.add(tour.getCurrentPrice());
        dates.add(now.format(formatter));


        // Передаем данные в модель для отображения на странице
        model.addAttribute("tour", tour);
        model.addAttribute("link", link);
        model.addAttribute("priceHistoryDates", dates);
        model.addAttribute("priceHistoryPrices", prices);
        model.addAttribute("priceDifference", priceDifference);
        model.addAttribute("oldPrice", oldPrice);

        return "tour-details";
    }


    // del tour
    @GetMapping("/tour/delete/{id}")
    public String deleteTourAndRelatedData(@PathVariable Long id) {
        // Поиск тура по ID
        Optional<TourEntity> optionalTour = tourRepository.findById(id);

        if (optionalTour.isEmpty()) {
            return "redirect:/tours"; // ошибка? пусто? редирект! возможно сделать 404 страницу
        }

        // Поиск всего и вся
        TourEntity tour = optionalTour.get();
        List<TourPriceHistoryEntity> tourPriceHistory = tour.getPriceHistory();
        LinkEntity link = tour.getLink();
        SelectorEntity selector = link.getSelectorEntity();

        // Удаление связанных данных
        tourPriseHistoryRepository.deleteAll(tourPriceHistory);
        tourRepository.delete(tour);
        linkRepository.delete(link);

        // Проверка, пустой ли which_site у селектора
        if (selector.getWhichSite() == null || selector.getWhichSite().isEmpty()) {
            selectorRepository.delete(selector);
        }

        return "redirect:/tours"; // возврат на главную страницу
    }


}
