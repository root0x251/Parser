package com.example.demo.controllers;

import com.example.demo.entity.tour.LogErrorEntity;
import com.example.demo.repository.tour.LogErrorRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errors")
public class ErrorViewController {

    private final LogErrorRepo logErrorRepo;

    public ErrorViewController(LogErrorRepo logErrorRepo) {
        this.logErrorRepo = logErrorRepo;
    }

    @GetMapping
    public String listAllTours(Model model) {
        model.addAttribute("logErrors", logErrorRepo.findAll());
        return "error";
    }

    @GetMapping("/sorted")
    public String listAllToursSorted(Model model) {
        model.addAttribute("logErrors", logErrorRepo.findAllSortedByTourName());
        return "error";
    }

}
