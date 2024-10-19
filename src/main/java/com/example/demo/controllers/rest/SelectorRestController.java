package com.example.demo.controllers.rest;

import com.example.demo.entity.LinkEntity;
import com.example.demo.entity.SelectorEntity;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.SelectorRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/selector")
public class SelectorRestController {

    private final SelectorRepo selectorRepo;
    private final LinkRepository linkRepository;

    @Autowired
    public SelectorRestController(SelectorRepo selectorRepo, LinkRepository linkRepository) {
        this.selectorRepo = selectorRepo;
        this.linkRepository = linkRepository;
    }

    // Add hotel link with selectors
    @PostMapping("/add")
    public String addUrlAndSelector(@RequestBody String test) {
        ObjectMapper objectMapper = new ObjectMapper();

        String link;
        String whichSite;
        String hotelSelector;
        String priceSelector;

        try {
            // Преобразуем строку JSON в объект JsonNode
            JsonNode jsonNode = objectMapper.readTree(test);

            // Извлекаем данные из JSON
            link = jsonNode.get("url").asText();
            whichSite = jsonNode.get("whichSite").asText();
            hotelSelector = jsonNode.get("hotelSelector").asText();
            priceSelector = jsonNode.get("priceSelector").asText();

            System.out.println("Link: " + link);
            System.out.println("whichSite: " + whichSite);
            System.out.println("hotelSelector: " + hotelSelector);
            System.out.println("priceSelector: " + priceSelector);

            // Выполняйте логику для обработки полученных данных
        } catch (Exception e) {
            return "Error";
        }

        LinkEntity linkEntity;
        SelectorEntity selectorEntity = null;

        if (!Objects.equals(whichSite, "")) {
            selectorEntity = selectorRepo.findByWhichSite(whichSite);
        }
        if (selectorEntity == null) {
            selectorEntity = new SelectorEntity(hotelSelector, priceSelector, whichSite);
            selectorRepo.save(selectorEntity);
        }
        linkEntity = new LinkEntity(link, selectorEntity);

        // Сохранить ссылку в базе
        linkRepository.save(linkEntity);
        return "done";
    }


}
