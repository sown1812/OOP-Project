package org.example;

import java.io.*;
import java.lang.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

public class Find_Tweets {
    static String filePath = "tweets.txt";
    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "D:\\Code\\Edgedriver\\msedgedriver.exe");

        EdgeOptions options = new EdgeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--headless");
        options.addArguments("--disable-notifications");

        WebDriver driver = new EdgeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));

        try {
            driver.get("https://twitter.com/login");

            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("text")));

            WebElement usernameField = driver.findElement(By.name("text"));
            usernameField.sendKeys("ahkey357@gmail.com");

            WebElement nextButton = driver.findElement(By.xpath("//span[text()='Next']"));
            nextButton.click();

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.name("text")));
                WebElement check = driver.findElement(By.name("text"));
                check.sendKeys("@tran_key666");
                WebElement nextButton_2 = driver.findElement(By.xpath("//span[text()='Next']"));
                nextButton_2.click();
            } catch (Exception e) {
                System.out.println("Element not found!");
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));

            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.sendKeys("Key123456");

            WebElement loginButton = driver.findElement(By.xpath("//span[text()='Log in']"));
            loginButton.click();

            wait.until(ExpectedConditions.urlContains("home"));

            String fileKOL = "KOL.txt";
            rf();

            try (BufferedReader br = new BufferedReader(new FileReader(fileKOL))) {
                String acc;
                while ((acc = br.readLine()) != null){
                    int spaceIndex = acc.indexOf(' ');
                    if (spaceIndex != -1) {
                        acc = acc.substring(0, spaceIndex);
                    }

                    driver.get(acc);
                    System.out.println(acc);

                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    HashSet<String> tweetUrls = new HashSet<>();

                    while(tweetUrls.size() < 10) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article[role='article']")));
                        List<WebElement> tweets = driver.findElements(By.cssSelector("article[role='article']"));

                        int tweetCount = Math.min(tweets.size(), 10);
                        System.out.println(tweetCount);
                        for (int j = 0; j < tweetCount; j++) {
                            WebElement tweet = tweets.get(j);
                            System.out.println(tweet);
                            WebElement tweetLinkElement = tweet.findElement(By.cssSelector("a[href*='/status/']"));
                            String tweetUrl = tweetLinkElement.getAttribute("href");
                            if(tweetUrls.size() < 10)
                                tweetUrls.add(tweetUrl);
                        }
                        js.executeScript("window.scrollBy(0, 2000);");
                    }
                    for (String tweetUrl : tweetUrls)
                        writeToFile(tweetUrl);
//                    new_Line();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            driver.quit();
        }
    }

    public static void rf() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
        } catch (IOException e) {
        }
    }

    public static void writeToFile(String url) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(url);
            writer.newLine();
        } catch (IOException e) {
        }
    }

    public static void new_Line() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
        } catch (IOException e) {
        }
    }
}
