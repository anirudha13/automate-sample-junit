package my.anirudha.samples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class SampleSeleniumTest {

    private static final Logger LOGGER = LogManager.getLogger(SampleSeleniumTest.class);

    @Test
    public void testBrowserStackLogin() throws Exception {
        /* =================== Prepare ================= */
        System.setProperty("webdriver.chrome.driver", "/Users/anirudha/bin/chromedriver");
        WebDriver webDriver = new ChromeDriver();

        /* =================== Execute & Verify ================= */
        LOGGER.info("Navigating to BrowserStack home page");
        webDriver.get("https://www.browserstack.com/");
        WebElement signInElement = webDriver.findElement(By.linkText("Sign in"));
        Assert.assertNotNull("No Sign In button found", signInElement);
        LOGGER.info("Clicking on Sign In button.");
        signInElement.click();
        WebElement emailElement = webDriver.findElement(By.id("user_email_login"));
        WebElement passwordElement = webDriver.findElement(By.id("user_password"));
        WebElement submitElement = webDriver.findElement(By.id("user_submit"));

        emailElement.sendKeys("anirudha.khanna@gmail.com");
        Thread.sleep(100);
        passwordElement.sendKeys("Automate@123");
        Thread.sleep(100);
        submitElement.click();

        webDriver.quit();
    }
}
