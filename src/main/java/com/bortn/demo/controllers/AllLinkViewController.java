package com.bortn.demo.controllers;


import com.bortn.demo.entity.tour.LinkEntity;
import com.bortn.demo.entity.tour.SelectorEntity;
import com.bortn.demo.repository.LinkRepository;
import com.bortn.demo.repository.SelectorRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("allLink")
public class AllLinkViewController {

    private final LinkRepository linkRepository;
    private final SelectorRepo selectorRepo;

    public AllLinkViewController(LinkRepository linkRepository, SelectorRepo selectorRepo) {
        this.linkRepository = linkRepository;
        this.selectorRepo = selectorRepo;
    }

    @GetMapping
    public String allLink(Model model) {
        model.addAttribute("links", linkRepository.findAll());
        return "allLink";
    }

    // del link and tour if exist
    @GetMapping("/delete/{id}")
    public String deleteTourAndRelatedData(@PathVariable Long id) {

        // нужно сделать поиск в туре по link_id
        Optional<LinkEntity> link = linkRepository.findById(id);
        if (link.isEmpty()) {
            return "redirect:/allLink";
        } else {
            SelectorEntity selector = link.get().getSelectorEntity();
            if (!selector.isTemplate()) {
                selectorRepo.delete(selector);
            }
            linkRepository.delete(link.get());
        }

//        Optional<TourEntity> tourEntity = tourRepository.searchByLinkId(id);
//        if (tourEntity.isEmpty()) {
//            return "redirect:/tours";
//        } else {
//            tourRepository.delete(tourEntity.get());
//            tourPriseHistoryRepository.deleteAll(tourEntity.get().getPriceHistory());
//        }

        return "redirect:/allLink"; // возврат на главную страницу
    }

}
