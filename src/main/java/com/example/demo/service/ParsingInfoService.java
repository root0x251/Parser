package com.example.demo.service;

import com.example.demo.entity.tour.ParserInfoEntity;
import com.example.demo.repository.tour.ParserInfoRepository;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ParsingInfoService {

    private final ParserInfoRepository parserInfoRepository;

    public ParsingInfoService(ParserInfoRepository parserInfoRepository) {
        this.parserInfoRepository = parserInfoRepository;
    }

    public void addParsingInfoService(Model model) {

        try {
            ParserInfoEntity parserInfoEntity = parserInfoRepository.findLast();

            String startParsing = parserInfoEntity.getStartParsingTime();
            String endParsing = parserInfoEntity.getEndParsingTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            LocalDateTime startParsingTime = LocalDateTime.parse(startParsing, formatter);
            LocalDateTime endParsingTime = LocalDateTime.parse(endParsing, formatter);

            Duration duration = Duration.between(startParsingTime, endParsingTime);

            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            // Вывод информации по парсингу
            model.addAttribute("startParsingTime", startParsingTime.format(formatter));
            model.addAttribute("endParsingTime", endParsingTime.format(formatter));
            model.addAttribute("totalTime", hours + " часов и " + minutes + " минут");
            model.addAttribute("passesCount", parserInfoEntity.getPassesCount());
            model.addAttribute("errorCount", parserInfoEntity.getErrorCount());
        } catch (NullPointerException ignore) {

        }

    }

}
