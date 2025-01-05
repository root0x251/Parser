package com.example.demo.controllers;

import com.example.demo.entity.tour.*;
import com.example.demo.repository.tour.*;
import com.example.demo.service.ParsingInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping({"/", "/tours"})
public class TourViewController {

    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;
    private final LinkRepository linkRepository;
    private final SelectorRepo selectorRepository;

    private final ParsingInfoService parsingInfoService;

    public TourViewController(TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository, LinkRepository linkRepository, SelectorRepo selectorRepository, ParsingInfoService parsingInfoService) {
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
        this.linkRepository = linkRepository;
        this.selectorRepository = selectorRepository;
        this.parsingInfoService = parsingInfoService;
    }

    // all tours
    @GetMapping
    public String listAllTours(Model model) {
        // Информация по парсингу для футера
        parsingInfoService.addParsingInfoService(model);
        // Вывод всех туров
        model.addAttribute("tours", tourRepository.findAll());
        return "tour-list";
    }

    // подробнее
    @GetMapping("/tour/{id}")
    public String getTourDetails(@PathVariable Long id,
                                 @RequestParam(defaultValue = "7days") String timeframe,
                                 Model model) {

        Optional<TourEntity> optionalTour = tourRepository.findById(id);
        if (optionalTour.isEmpty()) {
            return "error/404"; // шаблон с ошибкой
        }

        TourEntity tour = optionalTour.get();
        LinkEntity link = tour.getLink();

        List<TourPriceHistoryEntity> tourPriceHistory;

        // Фильтр за 7 дней
        if ("7days".equals(timeframe)) {
            tourPriceHistory = tourPriseHistoryRepository.findLastSevenPrises(tour.getId());
            Collections.reverse(tourPriceHistory);
        } else {
            // Фильтр за все время
            tourPriceHistory = tourPriseHistoryRepository.findAll(tour.getId());
        }

        // Информация о старой цене и разнице
        int oldPrice = 0;
        int priceDifference = 0;
        if (!tourPriceHistory.isEmpty()) {
            oldPrice = tourPriceHistory.get(tourPriceHistory.size() - 1).getOldPrice();
            priceDifference = tour.getCurrentPrice() - oldPrice;
        }

        // Преобразуем данные для графика
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

        // Передаем данные в модель
        model.addAttribute("tour", tour);
        model.addAttribute("link", link);
        model.addAttribute("priceHistoryDates", dates);
        model.addAttribute("priceHistoryPrices", prices);
        model.addAttribute("priceDifference", priceDifference);
        model.addAttribute("oldPrice", oldPrice);
        model.addAttribute("timeframe", timeframe); // Добавляем параметр фильтрации

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
