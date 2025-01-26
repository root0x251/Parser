package com.example.demo.controllers;

import com.example.demo.entity.tour.LinkEntity;
import com.example.demo.entity.tour.SelectorEntity;
import com.example.demo.repository.tour.LinkRepository;
import com.example.demo.repository.tour.SelectorRepo;
import com.example.demo.service.ParsingInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AddTourController {
    private final SelectorRepo selectorRepository;
    private final LinkRepository linkRepository;

    private final ParsingInfoService parsingInfoService;

    public AddTourController(SelectorRepo selectorRepository, LinkRepository linkRepository, ParsingInfoService parsingInfoService) {
        this.selectorRepository = selectorRepository;
        this.linkRepository = linkRepository;
        this.parsingInfoService = parsingInfoService;
    }

    // Показ формы для добавления ссылки и селектора
    @GetMapping("/addLinkSelector")
    public String showAddLinkSelectorForm(Model model) {
        // Получаем список всех whichSite из базы для выпадающего меню
        List<SelectorEntity> selectors = selectorRepository.findByNoTemplate();

        // Добавляем в модель список селекторов для выпадающего меню
        model.addAttribute("selectors", selectors);

        // Информация по парсингу
        parsingInfoService.addParsingInfoService(model);

        return "add-link-selector"; // шаблон для отображения формы
    }


    @PostMapping("/addLinkSelector")
    public String handleAddLinkSelectorForm(@RequestParam(required = false) Boolean useCheckbox,
                                            @RequestParam(value = "url", required = false) String url,
                                            @RequestParam(value = "existingWhichSiteId", required = false) Long existingWhichSiteId,
                                            @RequestParam(value = "whichSite", required = false) String whichSite,
                                            @RequestParam(value = "siteLogo", required = false) String siteLogo,
                                            @RequestParam(value = "hotelNameSelector", required = false) String hotelNameSelector,
                                            @RequestParam(value = "priceSelector", required = false) String priceSelector,
                                            @RequestParam(value = "tourStartDate", required = false) String tourStartDate,
                                            @RequestParam(value = "hotelAddress", required = false) String hotelAddress) {

        SelectorEntity selector;

        // Чекаем ссылку, дублируется или нет
        if (!linkRepository.existsByLink(url)) {
            // проверка чекбокс или выпадающий список, или проверка на существование
            if (useCheckbox != null && useCheckbox) {
                // добавление сайта без шаблона
                selector = new SelectorEntity(hotelNameSelector, priceSelector, whichSite, tourStartDate, hotelAddress, siteLogo, false);
                selectorRepository.save(selector);
            } else {
                // Проверка существования тура
                if (existingWhichSiteId != null) {
                    // если был выбран шаблон
                    selector = selectorRepository.findById(existingWhichSiteId).orElseThrow();
                } else {
                    // Создать новый селектор
                        selector = new SelectorEntity(hotelNameSelector, priceSelector, whichSite, tourStartDate, hotelAddress, siteLogo, true);
                    selectorRepository.save(selector);
                }
            }
        } else {
            // Если ссылка существует, редиректимся
            // TODO возможно сделать всплывающее окно!!!
            return "redirect:/addLinkSelector";
        }

        // Создание и сохранение LinkEntity
        LinkEntity link = new LinkEntity();
        link.setSelectorEntity(selector);
        link.setLink(url);
        linkRepository.save(link);

        return "redirect:/addLinkSelector"; // Перенаправление обратно на форму после сохранения
    }


}
