package com.example.baseballprediction.global.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WebDriverUtil {

	private static String webDriverPath;

	@Value("${my-env.chrome-driver.path}")
	public void setWebDriverPath(String path) {
		webDriverPath = path;
	}

	public static WebDriver getChromeDriver() {
		if (ObjectUtils.isEmpty(System.getProperty("webdriver.chrome.driver"))) {
			System.setProperty("webdriver.chrome.driver", webDriverPath);
		}

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("headless");

		WebDriver driver = new ChromeDriver(chromeOptions);

		return driver;
	}
}
