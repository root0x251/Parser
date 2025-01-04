package com.example.demo.controllers;

import com.example.demo.entity.tour.LogErrorEntity;
import com.example.demo.repository.tour.LogErrorRepo;
import com.example.demo.service.ParsingInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errors")
public class ErrorViewController {

    private final LogErrorRepo logErrorRepo;

    private final ParsingInfoService parsingInfoService;

    public ErrorViewController(LogErrorRepo logErrorRepo, ParsingInfoService parsingInfoService) {
        this.logErrorRepo = logErrorRepo;
        this.parsingInfoService = parsingInfoService;
    }

    @GetMapping
    public String listAllTours(Model model) {
        model.addAttribute("logErrors", logErrorRepo.findAll());
        // Инфо по парсингу
        parsingInfoService.addParsingInfoService(model);
        return "error";
    }

    @GetMapping("/sorted")
    public String listAllToursSorted(Model model) {
        model.addAttribute("logErrors", logErrorRepo.findAllSortedByTourName());
        return "error";
    }

}
