package com.bancreapay.bancreapay_tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String GRID_URL  = System.getProperty("selenium.hub.url", "http://selenium-hub:4444/wd/hub");
    private static final String BASE_URL  = "https://test-bancrea-centraladmin.efevoopay.com/";

    @BeforeMethod
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName(System.getProperty("browser", "chrome"));
        driver = new RemoteWebDriver(new URL(GRID_URL), caps);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void testLoginPageLoads() {
        driver.get(BASE_URL);

        // Espera a que el elemento de la página de login esté visible
        WebElement loginLink = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a.Login_linkLandingPage__kiYi0")
            )
        );

        Assert.assertTrue(loginLink.isDisplayed(),
            "La página de login no cargó correctamente — el elemento de login no es visible");

        Assert.assertEquals(loginLink.getText().trim(), "bancrea.com",
            "El texto del link de login no coincide");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}