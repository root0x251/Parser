package com.bortn.demo.controllers;

import com.bortn.demo.entity.tour.TourEntity;
import com.bortn.demo.repository.TourRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/archive")
public class ArchiveTourViewController {

    private final TourRepository tourRepository;


    public ArchiveTourViewController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }


    @GetMapping()
    public String allArchTour(Model model) {
        model.addAttribute("tours", tourRepository.searchByArchivedLink(true));
        return "arch-tour-list";
    }

    // убираем ссылку из архива
    @GetMapping("/{id}")
    public String archiveTour(@PathVariable Long id) {
        Optional<TourEntity> tourOptional = tourRepository.findById(id);
        if (tourOptional.isPresent()) {
            TourEntity tour = tourOptional.get();
            // меняем ссылку на не архив
            tour.getLink().setArchive(false);
            // обнуляем счетчик ошибок
            tour.getLink().setErrorCount(0);
            tourRepository.save(tour);
        }
        return "redirect:/archive";
    }


}
