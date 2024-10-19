package com.example.demo.job;

import com.example.demo.entity.LinkEntity;
import com.example.demo.entity.TourEntity;
import com.example.demo.entity.TourPriceHistoryEntity;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.LogErrorRepo;
import com.example.demo.repository.TourPriseHistoryRepository;
import com.example.demo.repository.TourRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ParseTour {

    // проверка, запущен ли парсер (нужен будет в будущем)
    public static AtomicBoolean isParserRunning = new AtomicBoolean(false);

    private String hotelName = "Null";
    private int priceInt = 0;

    private final LinkRepository linkRepository;
    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;

    private final LogErrorRepo logErrorRepo;

    @Autowired
    public ParseTour(LinkRepository linkRepository, TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository, LogErrorRepo logErrorRepo) {
        this.linkRepository = linkRepository;
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
        this.logErrorRepo = logErrorRepo;
    }

    @Scheduled(fixedDelay = 100_000)
    public void initParse() {
        isParserRunning.set(true);

        WebDriverManager.chromedriver().setup();
        WebDriver webDriver = null;
        ChromeOptions options = getChromeOptions();

        for (LinkEntity link : linkRepository.findAll()) {
            String selectorHotelName = link.getSelectorEntity().getHotelSelector();
            String selectorHotelPrice = link.getSelectorEntity().getPriceSelector();
            Long selectorID = link.getSelectorEntity().getId();

            try {
                webDriver = new ChromeDriver(options);
                sleep();
                webDriver.get(link.getLink());
                sleep();
                startParse(webDriver, selectorHotelName, selectorHotelPrice, selectorID);
                workWithDB(link);
            } catch (SessionNotCreatedException e) {
                errorLog("SessionNotCreatedException");
                webDriverQuit(webDriver, "SessionNotCreatedException");
            } catch (TimeoutException e) {
                errorLog("TimeoutException");
                webDriverQuit(webDriver, "SessionNotCreatedException");
            }

        }

        webDriverQuit(webDriver, "quit");

        isParserRunning.set(false);
        System.out.println("======================================= EXIT ======================================");

    }

    private void startParse(WebDriver webDriver, String selectorHotelName, String selectorHotelPrice, Long selectorID) {
        sleep();
        try {
            scrollDownAndUp(webDriver);
            hotelName = webDriver.findElement(By.xpath(selectorHotelName)).getText();
            priceInt = Integer.parseInt(webDriver.findElement(By.xpath(selectorHotelPrice)).getText().replaceAll("[^\\d.]", ""));
        } catch (InvalidSelectorException e) {
            hotelName = webDriver.findElement(By.cssSelector(selectorHotelName)).getText();
            priceInt = Integer.parseInt(webDriver.findElement(By.cssSelector(selectorHotelPrice)).getText().replaceAll("[^\\d.]", ""));
        } catch (NoSuchElementException e) {
            errorLog("NoSuchElementException", selectorID);
            webDriverQuit(webDriver, "NoSuchElementException");
        } catch (NumberFormatException exception) {
            errorLog("NumberFormatException", selectorID);
            webDriverQuit(webDriver, "NumberFormatException");
        } catch (TimeoutException exception) {
            errorLog("TimeoutException", selectorID);
            webDriverQuit(webDriver, "TimeoutException");
        }

        webDriverQuit(webDriver, "no error");
    }

    private void workWithDB(LinkEntity linkEntity) {
        // проверка на существование тура
        Optional<TourEntity> existingTourOptional = tourRepository.findByLink(linkEntity);

        if (existingTourOptional.isPresent()) {
            // Если тур существует, проверяем изменение цены
            TourEntity existingTour = existingTourOptional.get();
            int currentPriceFromDB = existingTour.getCurrentPrice();

            if (currentPriceFromDB != priceInt) {
                // Добавляем запись в историю изменений цены
                LocalDateTime now = LocalDateTime.now();

                // Добавляем запись в историю изменений цены
                TourPriceHistoryEntity priceHistory = new TourPriceHistoryEntity(now, currentPriceFromDB, existingTour);

                tourPriseHistoryRepository.save(priceHistory);

                // Обновляем текущую цену в туре
                existingTour.setCurrentPrice(priceInt);

                if (currentPriceFromDB < priceInt) {
                    existingTour.setPriceChange("цена увеличилась");
                } else {
                    existingTour.setPriceChange("цена уменьшилась");
                }
                tourRepository.save(existingTour);
            }
        } else {
            // Тур не существует — создаем новый тур
            TourEntity tour = new TourEntity(hotelName, priceInt, "без изменений", linkEntity);
            // Сохранение тура в базу данных
            tourRepository.save(tour);
        }
    }

    public void scrollDownAndUp(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Прокрутка вниз на 500 пикселей
        js.executeScript("window.scrollBy(0, 500)");

        // Небольшая пауза для видимости эффекта
        sleep();

        // Прокрутка обратно вверх на 500 пикселей
        js.executeScript("window.scrollBy(0, -500)");
    }

    private void webDriverQuit(WebDriver webDriver, String error) {
        System.out.println(error);
        if (webDriver != null) webDriver.quit();
    }

    private void errorLog(String errorCode) {
        System.out.println(errorCode);
    }

    private void errorLog(String errorCode, Long selectorID) {
        System.out.println("Selector ID = " + selectorID);
        System.out.println(errorCode);
    }

    private void sleep() {
        int rand = new Random().nextInt(5000) + 8000;
        System.out.println("=========== sleep " + rand / 1000 + " sec ===========");
        try {
            Thread.sleep(rand);
        } catch (InterruptedException e) {
            errorLog("Thread sleep exception");
        }
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");                // Включаем headless режим
        options.addArguments("--incognito");               // Включаем инкогнито
        options.addArguments("--disable-extensions");      // Отключаем расширения
        options.addArguments("--disable-gpu");             // Отключаем GPU
        options.addArguments("--no-sandbox");              // Отключаем песочницу
        options.addArguments("--disable-dev-shm-usage");   // Отключаем /dev/shm для сервера
        options.addArguments("--window-size=1920,1080");   // Устанавливаем размер окна браузера
        options.addArguments("--disable-blink-features=AutomationControlled"); // Отключаем обнаружение Selenium
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        // Меняем User-Agent на стандартный пользовательский
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.668.70 Safari/537.36");

        // Отключение автоматической идентификации Selenium через переменные
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        // Отключаем автоматизацию на уровне JS для более сложных проверок
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        return options;
    }

}
