package com.example.demo.controllers;

import com.example.demo.entity.LinkEntity;
import com.example.demo.entity.SelectorEntity;
import com.example.demo.entity.TourEntity;
import com.example.demo.entity.TourPriceHistoryEntity;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.SelectorRepo;
import com.example.demo.repository.TourPriseHistoryRepository;
import com.example.demo.repository.TourRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TourViewController {

    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;

    public TourViewController(TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository) {
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
    }

    @GetMapping("/tours")
    public String listAllTours(Model model) {
        model.addAttribute("tours", tourRepository.findAll());
        return "tour-list";
    }

    @GetMapping("/tour/{id}")
    public String getTourDetails(@PathVariable Long id, Model model) {
        Optional<TourEntity> optionalTour = tourRepository.findById(id);
        if (optionalTour.isEmpty()) {
            return "tour-not-found"; // шаблон с ошибкой
        }

        TourEntity tour = optionalTour.get();
        LinkEntity link = tour.getLink();
        List<TourPriceHistoryEntity> tourPriceHistory = tour.getPriceHistory();

        // Преобразуем данные из priceHistory для использования в графике
        List<String> dates = new ArrayList<>();
        List<Integer> prices = new ArrayList<>();

        for (TourPriceHistoryEntity history : tourPriceHistory) {
            dates.add(history.getDate().toString()); // Преобразуем дату в строку
            prices.add(history.getOldPrice()); // Добавляем цену
        }


        // Передаем данные в модель для отображения на странице
        model.addAttribute("tour", tour);
        model.addAttribute("link", link);
        model.addAttribute("priceHistoryDates", dates); // Список дат
        model.addAttribute("priceHistoryPrices", prices); // Список цен

        return "tour-details";  // Имя шаблона Thymeleaf (tour-details.html)
    }



}
