package io.testeryou.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CompareAndLinksTest {

    public static final String IMDB_COM = "https://www.imdb.com";
    private WebDriver driver;

    @Before
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\test\\resources\\geckodriver-v0.31.0-win64\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.get(IMDB_COM);
        driver.manage().window().maximize();
    }

    @Test
    public void compareTheCircus(){
        compare("The Circus");
    }

    @Test
    public void compareTheJazzSinger(){
        compare("The Jazz Singer");
    }

    @Test
    public void checkLinksTheCircus(){
        checkLinks("The Circus");
    }

    @Test
    public void checkLinksTheJazzSinger(){
        checkLinks("The Jazz Singer");
    }

    private void checkLinks(String movieName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        search(wait, movieName);
        driver.findElement(By.cssSelector(".sc-4b188491-0 > div:nth-child(1) > a:nth-child(1) > h3:nth-child(1)")).click();
        while (true) {
            List<WebElement> aTags = driver.findElements(By.xpath("/html/body/div[2]/div/div[2]/div[3]/div[1]/div[1]/div[2]/div[2]/*"));
            for (WebElement aTag : aTags) {
                if (aTag.getAttribute("class").equals("add-image"))
                    continue;
                String url = aTag.getAttribute("href");
                checkUrl(url);
            }
            try {
                WebElement galleryNext = driver.findElement(By.cssSelector("div.media_index_pagination:nth-child(1) > div:nth-child(2) > a:nth-child(2)"));
                galleryNext.click();
            } catch (NoSuchElementException e) {
                break;
            }
        }
    }
    private void search(WebDriverWait wait, String movieName) {
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"suggestion-search\"]")));
        search.sendKeys(movieName);
        WebElement movie = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#react-autowhatever-1--item-0 > a:nth-child(1) > div:nth-child(2) > div:nth-child(1)")));
        movie.click();
    }

    private void compare(String movieName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.findElement(By.xpath("//*[text()='Menu']")).click();
        WebElement oscars = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Oscars']")));
        oscars.click();
        driver.findElement(By.xpath("//*[text()='1929']")).click();
        driver.findElement(By.xpath("//*[text()='" + movieName + "']")).click();
        String expDirector = getDirector();
        String expWriter = getWriter();
        List<String> expStars = findStars();
        driver.findElement(By.id("home_img_holder")).click();
        search(wait, movieName);
        String director = getDirector();
        String writer = getWriter();
        List<String> stars = findStars();
        Assert.assertEquals(expDirector, director);
        Assert.assertEquals(expWriter, writer);
        Assert.assertEquals(expStars, stars);
    }
    private void checkUrl(String url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int respCode = urlConnection.getResponseCode();

            if (respCode >= 400) {
                Assert.fail("URL is broken." + url);
            } else {
                System.out.println(url);
                System.out.println(respCode);
            }
        } catch (IOException e) {
            Assert.fail("URL is broken." + url);
        }
    }

    private String getWriter() {
        return driver.findElement(By.cssSelector(".sc-fa02f843-0 > ul:nth-child(1) > li:nth-child(2) > div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)")).getText();
    }

    private String getDirector() {
        return driver.findElement(By.cssSelector(".sc-fa02f843-0 > ul:nth-child(1) > li:nth-child(1) > div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)")).getText();
    }

    private List<String> findStars() {
        List<String> allStars = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String star = driver.findElement(By.cssSelector(".sc-fa02f843-0 > ul:nth-child(1) > li:nth-child(3) > div:nth-child(2) > ul:nth-child(1) > li:nth-child(" + i + ") > a:nth-child(1)")).getText();
            allStars.add(star);
        }
        return allStars;
    }

    @After
    public void close() {
        driver.quit();
    }
}
