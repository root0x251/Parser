package com.example.demo.job;

import com.example.demo.entity.tour.LinkEntity;
import com.example.demo.entity.tour.TourEntity;
import com.example.demo.entity.tour.TourPriceHistoryEntity;
import com.example.demo.repository.tour.LinkRepository;
import com.example.demo.repository.tour.LogErrorRepo;
import com.example.demo.repository.tour.TourPriseHistoryRepository;
import com.example.demo.repository.tour.TourRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParseTour {

    // проверка, запущен ли парсер (нужен будет в будущем)
    public static AtomicBoolean isParserRunning = new AtomicBoolean(false);

    private String hotelName = "Null";
    private int priceInt = 0;
    private List<String> images;

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

//    @Scheduled(fixedDelay = 2_800_000)
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
                webDriverQuit(webDriver, "no error");
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

            System.out.println(hotelName);
            System.out.println(priceInt);
            // add images to list
            searchImage(webDriver);

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
    }

    private void searchImage(WebDriver webDriver) {
        // array with images
        images = new ArrayList<>();

        try {
            List<WebElement> biblioImages = webDriver.findElements(By.cssSelector(".image ul li.visible a.link"));
            List<WebElement> funsunImages = webDriver.findElements(By.cssSelector(".hotelInfoPhotos .box .main, .hotelInfoPhotos .box .photo"));

            // Регулярное выражение для извлечения URL из style
            Pattern urlPattern = Pattern.compile("url\\(\"(.*?)\"\\)");
            // biblio
            for (WebElement element : biblioImages) {
                String fullImageUrl = element.getAttribute("href");
                if (fullImageUrl != null) {
                    images.add(fullImageUrl);
                }
            }
            // funsun
            for (WebElement element : funsunImages) {
                String styleAttribute = element.getAttribute("style");
                if (styleAttribute != null) {
                    Matcher matcher = urlPattern.matcher(styleAttribute);
                    if (matcher.find()) {
                        String imageUrl = matcher.group(1);
                        images.add(imageUrl);
                    }
                }
            }
        } finally {
            System.out.println("images done");
        }

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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

                // Добавляем запись в историю изменений цены
                TourPriceHistoryEntity priceHistory = new TourPriceHistoryEntity(now.format(formatter), currentPriceFromDB, existingTour);

                tourPriseHistoryRepository.save(priceHistory);

                // Обновляем текущую цену в туре
                existingTour.setCurrentPrice(priceInt);

                if (currentPriceFromDB < priceInt) {
                    existingTour.setPriceChange("Цена увеличилась");
                } else {
                    existingTour.setPriceChange("Цена уменьшилась");
                }
                tourRepository.save(existingTour);
            }
        } else {
            // Тур не существует — создаем новый тур
            TourEntity tour = new TourEntity(hotelName, priceInt, "без изменений", images, linkEntity);
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
        if (images != null && !images.isEmpty()) {
            images.clear();
        }
        if (webDriver != null) {
            webDriver.quit();
        }
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
