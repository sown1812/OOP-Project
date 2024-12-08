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
import java.util.List;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.time.Duration;

public class Find_KOL {
    static String filePath = "KOL.txt";
    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "D:\\Code\\Edgedriver\\msedgedriver.exe");
        Log L = new Log();
        EdgeOptions options = new EdgeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--headless");
        options.addArguments("--disable-notifications");

        WebDriver driver = new EdgeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));

        try {
            L.Log_in(driver, wait);
            wait.until(ExpectedConditions.urlContains("home"));

            String hashtag = "blockchain";
            driver.get("https://twitter.com/search?q=%23" + hashtag + "&src=recent_search_click&f=user");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            HashSet<String> userProfileUrls = new HashSet<>();

            while(userProfileUrls.size() < 200) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[role='button']")));
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
                System.out.println(userProfileUrls.size());
                js.executeScript("window.scrollBy(0, 3300);");
            }

            rf();

            for (String userProfileUrl : userProfileUrls) {
                try {
                    driver.get(userProfileUrl);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/verified_followers']")));

                    WebElement followersElement = driver.findElement(By.cssSelector("a[href*='/verified_followers']"));
                    String followersText = followersElement.getText();

                    int followersCount = parseFollowersCount(followersText);
                    System.out.println(followersText);
                    System.out.println(followersCount);

                    if (followersCount > 50000) {
                        writeToFile(userProfileUrl, followersCount);
                    }

                } catch (Exception e) {
                    System.out.println("Error processing tweet: " + e.getMessage());
                    continue;
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
