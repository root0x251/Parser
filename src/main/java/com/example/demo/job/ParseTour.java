package com.example.demo.job;

import com.example.demo.entity.tour.*;
import com.example.demo.repository.tour.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ParseTour {

    private final ChromeOptions options = getChromeOptions();

    // проверка, запущен ли парсер (нужен будет в будущем)
    public static AtomicBoolean isParserRunning = new AtomicBoolean(false);

    // константы для минимальной цены и таймера
    private static final int MIN_PRICE_THRESHOLD = 150000;
    private static final int MIN_SLEEP_MS = 8000;
    private static final int MAX_SLEEP_MS = 13000;

    // Инфо по туру
    private String hotelName = "Null";
    private int priceInt = 0;
    private String hotelAddress = "Null";
    private String tourStartDate = "Null";
    private String siteLogo = "Null";

    // информация для ParsingInfo, сбор метрик
    private long errorCounter = 0;
    private long passesCounter = 0;

    private final LinkRepository linkRepository;
    private final TourRepository tourRepository;
    private final TourPriseHistoryRepository tourPriseHistoryRepository;

    private final LogErrorRepo logErrorRepo;

    private final ParserInfoRepository parserInfoRepository;

    @Autowired
    public ParseTour(LinkRepository linkRepository, TourRepository tourRepository, TourPriseHistoryRepository tourPriseHistoryRepository,
                     LogErrorRepo logErrorRepo, ParserInfoRepository parserInfoRepository) {
        this.linkRepository = linkRepository;
        this.tourRepository = tourRepository;
        this.tourPriseHistoryRepository = tourPriseHistoryRepository;
        this.logErrorRepo = logErrorRepo;
        this.parserInfoRepository = parserInfoRepository;
    }

    @Scheduled(fixedDelay = 2_800_000)
    public void initParse() {
        String startParsingTime = currentDate();
        isParserRunning.set(true);

        WebDriverManager.chromedriver().setup();

        for (LinkEntity link : linkRepository.findAll()) {
            passesCounter++;
            WebDriver webDriver = null;
            System.out.println("======== New parser ========");
            try {
                webDriver = new ChromeDriver(options);
                webDriver.get(link.getLink());

                if (link.getSelectorEntity().getSiteLogo().isEmpty()) {
                    siteLogo = "https://15.ie/wp-content/uploads/2018/03/NoLogo-Logo.png";
                } else {
                    siteLogo = link.getSelectorEntity().getSiteLogo();
                }

                startParse(webDriver, link.getSelectorEntity().getHotelSelector(),
                        link.getSelectorEntity().getPriceSelector(), link.getLink(),
                        link.getSelectorEntity().getTourStartDateSelector(),
                        link.getSelectorEntity().getHotelAddressSelector());
                if (priceInt < MIN_PRICE_THRESHOLD) {
                    errorLog("Low Price", hotelName, link.getLink());
                } else {
                    workWithDB(link);
                }
            } catch (Exception e) {
                errorLog(e.getClass().getSimpleName() + " on Start parsing");
            } finally {
                if (webDriver != null) {
                    webDriverQuit(webDriver);
                }
                hotelName = "Null";
                priceInt = 0;
                hotelAddress = "Null";
                tourStartDate = "Null";
            }
        }
        parserInfoRepository.save(new ParserInfoEntity(startParsingTime, currentDate(), passesCounter, errorCounter));
        passesCounter = 0;
        errorCounter = 0;

        System.out.println("======== End parser ========");
        isParserRunning.set(false);
    }

    private void startParse(WebDriver webDriver, String selectorHotelName, String selectorHotelPrice, String tourLink,
                            String selectorTourStartDate, String selectorHotelAddress) {
        sleep(webDriver);

        try {
            scrollDownAndUp(webDriver);

            hotelName = webDriver.findElement(By.xpath(selectorHotelName)).getText();
            priceInt = Integer.parseInt(webDriver.findElement(By.xpath(selectorHotelPrice)).getText().replaceAll("[^\\d.]", ""));
            hotelAddress = webDriver.findElement(By.xpath(selectorHotelAddress)).getText();
            tourStartDate = webDriver.findElement(By.xpath(selectorTourStartDate)).getText();

        } catch (NoSuchElementException | NumberFormatException | TimeoutException e) {
            errorLog(e.getClass().getSimpleName(), hotelName, tourLink);
            webDriverQuit(webDriver);
        }
    }

    private void workWithDB(LinkEntity linkEntity) {
        // проверка на существование тура
        Optional<TourEntity> existingTourOptional = tourRepository.findByLink(linkEntity);

        if (existingTourOptional.isPresent()) {
            // Если тур существует, обновляем его данные
            updateExistingTour(existingTourOptional.get());
        } else {
            // Если тур не существует, создаем новый
            createNewTour(linkEntity);
        }
    }

    private void updateExistingTour(TourEntity existingTour) {
        int currentPriceFromDB = existingTour.getCurrentPrice();

        if (currentPriceFromDB != priceInt) {
            // Добавляем запись в историю изменений цены
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            TourPriceHistoryEntity priceHistory = new TourPriceHistoryEntity(now.format(formatter), currentPriceFromDB, existingTour);
            tourPriseHistoryRepository.save(priceHistory);

            // Обновляем текущую цену в туре
            existingTour.setCurrentPrice(priceInt);
            existingTour.setPriceChange(currentPriceFromDB < priceInt ? "Цена увеличилась" : "Цена уменьшилась");

            tourRepository.save(existingTour);
        }
    }

    private void createNewTour(LinkEntity linkEntity) {
        TourEntity tour = new TourEntity(hotelName, priceInt, priceInt, "Без изменений", hotelAddress, tourStartDate, linkEntity, siteLogo);
        // Сохранение тура в базу данных
        tourRepository.save(tour);
    }


    public void scrollDownAndUp(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Прокрутка вниз на 500 пикселей
        js.executeScript("window.scrollBy(0, 1500)");
        // Небольшая пауза для видимости эффекта
        sleep(driver);
        // Прокрутка обратно вверх на 500 пикселей
        js.executeScript("window.scrollBy(0, -1500)");
    }

    private void webDriverQuit(WebDriver webDriver) {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    public void errorLog(String errorCode) {
        errorCounter++;
        if (!logErrorRepo.existsByDate(currentDate())) {
            LogErrorEntity logErrorEntity = new LogErrorEntity(errorCode, currentDate());
            logErrorRepo.save(logErrorEntity);
        }
    }

    private void errorLog(String errorCode, String hotelName, String tourLink) {
        errorCounter++;
        if (!logErrorRepo.existsByDate(currentDate())) {
            LogErrorEntity logErrorEntity = new LogErrorEntity(errorCode, currentDate(), hotelName, tourLink);
            logErrorRepo.save(logErrorEntity);
        }
    }

    private void sleep(WebDriver webDriver) {
        int rand = new Random().nextInt(MIN_SLEEP_MS) + MAX_SLEEP_MS;
        try {
            Thread.sleep(rand);
        } catch (InterruptedException e) {
            errorLog(e.getClass().getSimpleName() + " on Timer");
            webDriverQuit(webDriver);
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
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        // Меняем User-Agent на стандартный пользовательский
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.116 Safari/537.36");
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

    private String currentDate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return now.format(formatter);
    }

}