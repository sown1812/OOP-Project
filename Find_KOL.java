package org.example;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;s
import java.util.List;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.time.Duration;

public class Find_KOL {
    static String filePath = "KOL.txt";
    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "D:\\Code\\Edgedriver\\msedgedriver.exe");

        EdgeOptions options = new EdgeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--headless");
        options.addArguments("--disable-notifications");

        WebDriver driver = new EdgeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));

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

            String hashtag = "blockchain";
            driver.get("https://twitter.com/search?q=%23" + hashtag + "&src=typed_query");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            HashSet<String> userProfileUrls = new HashSet<>();

            for (int i = 0; i < 10; i++) {
                js.executeScript("window.scrollBy(0, 2000);");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article")));
                List<WebElement> tweets = driver.findElements(By.cssSelector("article"));
                System.out.println(tweets.size());
                for (WebElement tweet : tweets){
                    WebElement userElement = tweet.findElement(By.cssSelector("div[dir='ltr'] span"));

                    String UserName = userElement.getText();
                    WebElement userProfileLink = tweet.findElement(By.cssSelector("a[href*='/']"));

                    String userProfileUrl = userProfileLink.getAttribute("href");
                    userProfileUrls.add(userProfileUrl);
                }
            }

            rf();

            for (String userProfileUrl : userProfileUrls) {
                try {
                    driver.get(userProfileUrl);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/verified_followers']")));

                    WebElement followersElement = driver.findElement(By.cssSelector("a[href*='/verified_followers']"));
                    String followersText = followersElement.getText();

                    int followersCount = parseFollowersCount(followersText);
                    System.out.println(followersText);
                    System.out.println(followersCount);

                    if (followersCount > 100000) {
                        writeToFile(userProfileUrl, followersCount);
                    }

                } catch (Exception e) {
                    System.out.println("Error processing tweet: " + e.getMessage());
                }
            }
        } finally {
            driver.quit();
        }
    }

    private static int parseFollowersCount(String followersText) {
        followersText = followersText.replace(",", "").toLowerCase();
        followersText = followersText.replace(" followers", "").trim();

        if (followersText.contains("k")) {
            followersText = followersText.replace("k", "").trim();
            return (int) (Float.parseFloat(followersText) * 1000);
        } else if (followersText.contains("m")) {
            followersText = followersText.replace("m", "").trim();
            return (int) (Float.parseFloat(followersText) * 1000000);
        }

        return Integer.parseInt(followersText);
    }

    public static void rf() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
        } catch (IOException e) {
        }
    }

    public static void writeToFile(String KOL, int x) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(KOL);
            writer.write(" ");
            writer.write(String.valueOf(x));
            writer.newLine();
            System.out.println("Dữ liệu đã được ghi vào file: ");
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi vào file: " + e.getMessage());
        }
    }
}
