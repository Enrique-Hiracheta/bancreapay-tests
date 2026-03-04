package com.bancreapay.utils; // ← cambia el paquete según tu estructura

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * DriverManager — maneja la creación del RemoteWebDriver.
 *
 * Lee las siguientes System Properties (pasadas desde Jenkins vía Maven):
 *   -Dbrowser=chrome|firefox|edge      (default: chrome)
 *   -Dselenium.hub.url=http://...      (default: localhost:4444/wd/hub)
 *   -Dheadless=true|false              (default: false)
 *
 * Uso en cada clase de test:
 *   private WebDriver driver;
 *
 *   @BeforeClass
 *   public void setUp() {
 *       driver = DriverManager.createDriver();
 *   }
 *
 *   @AfterClass
 *   public void tearDown() {
 *       DriverManager.quitDriver(driver);
 *   }
 */
public class DriverManager {

    // Lee los parámetros que Jenkins pasa via -D
    private static final String BROWSER         = System.getProperty("browser", "chrome");
    private static final String HUB_URL         = System.getProperty("selenium.hub.url", "http://localhost:4444/wd/hub");
    private static final boolean HEADLESS       = Boolean.parseBoolean(System.getProperty("headless", "false"));

    // Timeouts estándar
    private static final int IMPLICIT_WAIT_SECS = 10;
    private static final int PAGE_LOAD_SECS     = 30;

    private DriverManager() {}

    /**
     * Crea un RemoteWebDriver apuntando al Selenium Grid.
     * Lanza RuntimeException si el browser no es soportado o la URL es inválida.
     */
    public static WebDriver createDriver() {
        System.out.printf("🌐 Conectando al Grid: %s | Browser: %s | Headless: %s%n",
                HUB_URL, BROWSER, HEADLESS);

        try {
            URL hubUrl = new URL(HUB_URL);
            WebDriver driver = buildDriver(hubUrl);
            configureTimeouts(driver);
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("❌ URL del Selenium Hub inválida: " + HUB_URL, e);
        }
    }

    /**
     * Cierra el driver de forma segura (evita NullPointerException).
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("⚠️ Error al cerrar el driver: " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Métodos privados
    // -------------------------------------------------------------------------

    private static WebDriver buildDriver(URL hubUrl) {
        switch (BROWSER.toLowerCase().trim()) {
            case "chrome":
                return new RemoteWebDriver(hubUrl, buildChromeOptions());
            case "firefox":
                return new RemoteWebDriver(hubUrl, buildFirefoxOptions());
            case "edge":
                return new RemoteWebDriver(hubUrl, buildEdgeOptions());
            default:
                throw new RuntimeException("❌ Browser no soportado: '" + BROWSER +
                        "'. Usa: chrome, firefox o edge.");
        }
    }

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (HEADLESS) options.addArguments("--headless=new");
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080"
        );
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (HEADLESS) options.addArguments("--headless");
        options.addArguments("--width=1920", "--height=1080");
        return options;
    }

    private static EdgeOptions buildEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (HEADLESS) options.addArguments("--headless=new");
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080"
        );
        return options;
    }

    private static void configureTimeouts(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SECS));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_SECS));
    }
}
