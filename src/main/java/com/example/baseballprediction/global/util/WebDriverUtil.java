package com.example.baseballprediction.global.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WebDriverUtil {
    private static final String WEB_DRIVER_PATH = "src/main/resources/chromedriver";

    public static WebDriver getChromeDriver() {
        if (ObjectUtils.isEmpty(System.getProperty("webdriver.chrome.driver"))) {
            System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);
        }

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("headless");

        WebDriver driver = new ChromeDriver(chromeOptions);

        return driver;
    }
}
