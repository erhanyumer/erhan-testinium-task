package io.testinium.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Task1Tests {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions action;
    private String excelFilePath = System.getProperty("user.dir") + "\\test\\resources\\keywords.xlsx";

    @Before
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\test\\resources\\geckodriver-v0.31.0-win64\\geckodriver.exe");
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        action = new Actions(driver);
        driver.get("https://www.beymen.com/");
        driver.manage().window().maximize();
    }

    @Test
    public void testBeymen() throws IOException {
        // Verify home page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".beymen-logo")));

        // Read keywords from Excel
        FileInputStream file = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        String shortsKeyword = sheet.getRow(0).getCell(0).getStringCellValue();
        String shirtKeyword = sheet.getRow(1).getCell(0).getStringCellValue();
        file.close();

        // Enter "shorts" into the search box and delete it
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".default-input.o-header__search--input")));
        searchBox.sendKeys(shortsKeyword);
        searchBox.clear();

        // Enter "shirt" into the search box and press Enter
        searchBox.sendKeys(shirtKeyword);
        searchBox.sendKeys(Keys.ENTER);

        // Select a random product
        List<WebElement> products = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".o-productList__item")));
        Random rand = new Random();
        WebElement randomProduct = products.get(rand.nextInt(products.size()));
        randomProduct.click();

        // Get product details
        WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".o-productDetail__title")));
        WebElement productPrice = driver.findElement(By.cssSelector(".m-price__new"));
        String productDetails = "Product: " + productTitle.getText() + "\nPrice: " + productPrice.getText();

        // Write product details to txt file
        BufferedWriter writer = new BufferedWriter(new FileWriter("productDetails.txt"));
        writer.write(productDetails);
        writer.close();

        // Add product to basket
        WebElement addToBasketButton = driver.findElement(By.cssSelector(".o-productDetail__btnBasket"));
        addToBasketButton.click();

        // Verify price in basket
        WebElement basketPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".m-basket__productInfo .m-price__new")));
        Assert.assertEquals(productPrice.getText(), basketPrice.getText());

        // Increase product quantity to 2
        WebElement quantityDropdown = driver.findElement(By.cssSelector(".m-basket__quantity .m-select__select"));
        quantityDropdown.click();
        WebElement quantityOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".m-select__select option[value='2']")));
        quantityOption.click();

        // Verify quantity is 2
        WebElement quantity = driver.findElement(By.cssSelector(".m-basket__quantity .m-select__select"));
        Assert.assertEquals("2", quantity.getAttribute("value"));

        // Remove product from basket
        WebElement removeButton = driver.findElement(By.cssSelector(".m-basket__productRemove"));
        removeButton.click();

        // Verify basket is empty
        WebElement emptyBasketMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".m-basket__emptyBasketTitle")));
        Assert.assertTrue(emptyBasketMessage.isDisplayed());
    }

    @After
    public void close() {
        driver.quit();
    }
}

