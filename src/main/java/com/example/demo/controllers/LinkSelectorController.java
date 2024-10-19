package com.example.demo.controllers;

import com.example.demo.entity.LinkEntity;
import com.example.demo.entity.SelectorEntity;
import com.example.demo.form.LinkSelectorForm;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.SelectorRepo;
import com.github.dockerjava.api.model.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class LinkSelectorController {
    private final SelectorRepo selectorRepository;
    private final LinkRepository linkRepository;

    public LinkSelectorController(SelectorRepo selectorRepository, LinkRepository linkRepository) {
        this.selectorRepository = selectorRepository;
        this.linkRepository = linkRepository;
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

        return "add-link-selector"; // шаблон для отображения формы
    }

    // Обработка формы
    @PostMapping("/addLinkSelector")
    public String handleAddLinkSelectorForm(@ModelAttribute LinkSelectorForm form) {
        SelectorEntity selector;

        // Проверяем, был ли выбран существующий whichSite из выпадающего меню или введено новое значение
        if (form.getExistingWhichSiteId() != null) {
            // Выбрано существующее значение whichSite
            selector = selectorRepository.findById(form.getExistingWhichSiteId()).orElseThrow();
        } else {
            // Введено новое значение whichSite
            selector = new SelectorEntity();
            selector.setHotelSelector(form.getHotelNameSelector());
            selector.setPriceSelector(form.getPriceSelector());
            selector.setWhichSite(form.getWhichSite());
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
