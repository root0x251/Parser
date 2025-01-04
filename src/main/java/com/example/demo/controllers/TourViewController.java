package com.example.demo.controllers;

import com.example.demo.entity.tour.*;
import com.example.demo.repository.tour.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"/", "/tours"})
public class TourViewController {

    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;
    private final LinkRepository linkRepository;
    private final SelectorRepo selectorRepository;
    private final ParserInfoRepository parserInfoRepository;

    public TourViewController(TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository, LinkRepository linkRepository, SelectorRepo selectorRepository, ParserInfoRepository parserInfoRepository) {
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
        this.linkRepository = linkRepository;
        this.selectorRepository = selectorRepository;
        this.parserInfoRepository = parserInfoRepository;
    }

    // all tours
    @GetMapping
    public String listAllTours(Model model) {

        // Информация для футера
        try {
            ParserInfoEntity parserInfoEntity = parserInfoRepository.findLast();

            String startParsing = parserInfoEntity.getStartParsingTime();
            String endParsing = parserInfoEntity.getEndParsingTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            LocalDateTime startParsingTime = LocalDateTime.parse(startParsing, formatter);
            LocalDateTime endParsingTime = LocalDateTime.parse(endParsing, formatter);

            Duration duration = Duration.between(startParsingTime, endParsingTime);

            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            // Вывод информации по парсингу
            model.addAttribute("startParsingTime", startParsingTime.format(formatter));
            model.addAttribute("endParsingTime", endParsingTime.format(formatter));
            model.addAttribute("totalTime", hours + " часов и " + minutes + " минут");
            model.addAttribute("passesCount", parserInfoEntity.getPassesCount());
            model.addAttribute("errorCount", parserInfoEntity.getErrorCount());
        } catch (NullPointerException ignore) {

        }


        // Вывод всех туров
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

        int oldPrice = 0;
        int priceDifference = 0;
        if (!tourPriceHistory.isEmpty()) {
            oldPrice = tourPriceHistory.get(tourPriceHistory.size() - 1).getOldPrice();
            priceDifference = tour.getCurrentPrice() - oldPrice;
        }

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
