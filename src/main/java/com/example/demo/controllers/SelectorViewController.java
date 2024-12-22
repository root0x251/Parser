package com.example.demo.controllers;

import com.example.demo.entity.tour.SelectorEntity;
import com.example.demo.repository.tour.SelectorRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/selector")
public class SelectorViewController {

    private final SelectorRepo selectorRepo;

    public SelectorViewController(SelectorRepo selectorRepo) {
        this.selectorRepo = selectorRepo;
    }


    @GetMapping()
    public String getAllSelectors(Model model) {
        model.addAttribute("selectors", selectorRepo.findAll());
        return "selectors";
    }

    // Форма редактирования селектора
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        SelectorEntity selector = selectorRepo.findById(id)
                .orElse(null);
        if (selector == null) {
            return "redirect:/selector";
        }

        model.addAttribute("selector", selector);
        return "edit-selector";
    }

    // Обновление селектора
    @PostMapping("/update/{id}")
    public String updateSelector(@PathVariable("id") Long id,
                                 @RequestParam("whichSite") String whichSite,
                                 @RequestParam("hotelSelector") String hotelSelector,
                                 @RequestParam("priceSelector") String priceSelector) {
        // поиск селектора по ID
        Optional<SelectorEntity> selectorOptional = selectorRepo.findById(id);

        // проверка на существование селектора
        if (selectorOptional.isEmpty()) {
            return "redirect:/selector";
        }

        SelectorEntity selector = selectorOptional.get();

        // обновление данных
        selector.setWhichSite(whichSite);
        selector.setHotelSelector(hotelSelector);
        selector.setPriceSelector(priceSelector);

        selectorRepo.save(selector);

        return "redirect:/selector";
    }


}
