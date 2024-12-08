package org.example;

import java.io.*;
import java.lang.*;

import org.openqa.selenium.NoSuchElementException;
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

public class Find_retweet {
    static String filePath = "edge2.txt";
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

            String fileKOL = "tweets.txt";
            rf();

            try (BufferedReader br = new BufferedReader(new FileReader(fileKOL))) {
                String tweet;
                while ((tweet = br.readLine()) != null) {
                    String userAd = tweet.split("/")[3];
                    tweet = tweet + "/retweets";
                    driver.get(tweet);
                    System.out.println(tweet);
                    System.out.println(userAd);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    HashSet<String> userProfileUrls = new HashSet<>();

                    Number scrollPositionBefore = (Number) js.executeScript("return window.scrollY");
                    js.executeScript("window.scrollBy(0, 100);");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Number scrollPositionAfter = (Number) js.executeScript("return window.scrollY");

                    while(!scrollPositionBefore.equals(scrollPositionAfter)) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("button[role='button']")));
                        List<WebElement> users = driver.findElements(By.cssSelector("button[role='button']"));

                        System.out.println(users.size());
                        for (WebElement user : users){
                            try {
                                WebElement userProfileLink = user.findElement(By.cssSelector("a[href*='/']"));
                                String userProfileUrl = userProfileLink.getAttribute("href");
                                if (userProfileUrl != null && userProfileUrl.startsWith("https://x.com/")) {
                                    System.out.println(userProfileUrl);
                                    userProfileUrls.add(userProfileUrl);
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }

                        js.executeScript("window.scrollBy(0, 1000);");

                        scrollPositionBefore = (Number) js.executeScript("return window.scrollY");
                        js.executeScript("window.scrollBy(0, 100);");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scrollPositionAfter = (Number) js.executeScript("return window.scrollY");
                    }
                    for (String user : userProfileUrls) {
                        writeToFile(tweet);
                        writeToFile(" ");
                        writeToFile(user);
                        writeToFile("\n");
                    }
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
