package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TourDateFormatService {

    public Map<String, String> forFunSun(String inputData) {
//        С 03 марта・11 ночей・2 взр
        Map<String, String> tourDateInfoMap = new HashMap<>();

        Pattern pattern = Pattern.compile("С (\\d{2} \\S+)・(\\d+) ночей");
        Matcher matcher = pattern.matcher(inputData);

        if (matcher.find()) {
            tourDateInfoMap.put("date", matcher.group(1));
            tourDateInfoMap.put("countNight", matcher.group(2));
        } else {
            // todo не забыть прописать Error log

            tourDateInfoMap.put("date", "00.00.00");
            tourDateInfoMap.put("countNight", "0");
        }

        return tourDateInfoMap;
    }

}
