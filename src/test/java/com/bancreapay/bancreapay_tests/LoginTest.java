package com.bancreapay.bancreapay_tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.net.URL;

public class LoginTest {

    private WebDriver driver;
    // Selenium Grid corre en el contenedor, desde Jenkins apunta al hub
    private static final String GRID_URL = "http://selenium-hub:4444/wd/hub";

    @BeforeMethod
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName("chrome");
        driver = new RemoteWebDriver(new URL(GRID_URL), caps);
    }

    @Test
    public void testLoginPageLoads() {
        driver.get("https://test-bancrea-centraladmin.efevoopay.com/");
        Assert.assertTrue(driver.getTitle().contains("Login"), 
            "El título de la página debe contener 'Login'");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}