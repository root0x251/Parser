package com.example.demo.controllers;

import com.example.demo.entity.tour.LinkEntity;
import com.example.demo.entity.tour.SelectorEntity;
import com.example.demo.form.LinkSelectorForm;
import com.example.demo.repository.tour.LinkRepository;
import com.example.demo.repository.tour.SelectorRepo;
import com.example.demo.service.ParsingInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        // Создаем объект для передачи данных в форму
        LinkSelectorForm form = new LinkSelectorForm();

        // Получаем список всех whichSite из базы для выпадающего меню
        List<SelectorEntity> selectors = selectorRepository.findAll();

        // Добавляем в модель пустую форму и список селекторов для выпадающего меню
        model.addAttribute("form", form);
        model.addAttribute("selectors", selectors);

        // Инфо по парсингу
        parsingInfoService.addParsingInfoService(model);

        return "add-link-selector"; // шаблон для отображения формы
    }

    // Обработка формы
    @PostMapping("/addLinkSelector")
    public String handleAddLinkSelectorForm(@ModelAttribute LinkSelectorForm form) {
        SelectorEntity selector;

        // Проверка на существование одинаковых ссылок
        if (linkRepository.existsByLink(form.getUrl())) {
            return "redirect:/addLinkSelector";
        }

        // Проверяем, был ли выбран существующий whichSite из выпадающего меню или введено новое значение
        if (form.getExistingWhichSiteId() != null) {
            // Выбрано существующее значение whichSite
            selector = selectorRepository.findById(form.getExistingWhichSiteId()).orElseThrow();
        } else {
            // Введено новое значение whichSite
            selector = new SelectorEntity(form.getHotelNameSelector(), form.getPriceSelector(), form.getWhichSite(), form.getTourStartDate(), form.getHotelAddress());
            selectorRepository.save(selector);
        }

        // Создаем и сохраняем объект Link, связанный с Selector
        LinkEntity link = new LinkEntity();
        link.setSelectorEntity(selector);
        link.setLink(form.getUrl());
        linkRepository.save(link);

        return "redirect:/addLinkSelector"; // Перенаправление обратно на форму после сохранения
    }

}
