package com.bortn.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TourDateFormatService {

    public Map<String, String> forFunSun(String inputData) {
//        С 03 марта・11 ночей・2 взр
//        Pattern pattern = Pattern.compile("С (\\d{2} \\S+)・(\\d+) ночей");
        Map<String, String> tourDateInfoMap = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\d{1,2}) ([а-яА-Я]+) - \\d{1,2} [а-яА-Я]+ • (\\d+) ночей");
        Matcher matcher = pattern.matcher(inputData);

        Map<String, String> months = new HashMap<>();
        months.put("янв", "01");
        months.put("фев", "02");
        months.put("мар", "03");
        months.put("апр", "04");
        months.put("май", "05");
        months.put("июн", "06");
        months.put("июл", "07");
        months.put("авг", "08");
        months.put("сен", "09");
        months.put("окт", "10");
        months.put("ноя", "11");
        months.put("дек", "12");

        if (matcher.find()) {
            String day = matcher.group(1);
            String month = matcher.group(2);
            String formattedDate = String.format("%02d.%s", Integer.parseInt(day), months.get(month.toLowerCase()));

            tourDateInfoMap.put("date", formattedDate);
            tourDateInfoMap.put("countNight", matcher.group(3));
        } else {
            // todo не забыть прописать Error log

            tourDateInfoMap.put("date", "00.00.00");
            tourDateInfoMap.put("countNight", "0");
        }

        return tourDateInfoMap;
    }

}
