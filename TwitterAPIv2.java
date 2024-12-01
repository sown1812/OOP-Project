package com.example.project1.OOP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitterAPIv2 {
    public static void main(String[] args) {
        String bearerToken = "AAAAAAAAAAAAAAAAAAAAAJkmxQEAAAAAANjmUkRW3EilbYifz6NOTPbONvA%3D2di7SlPEPlsJg8L2sYOEl1O5lJf5y7Hq4ZdDcdprnWi4mnLO8R"; // Thay bằng Bearer Token của bạn
        String username = "elonmusk"; // Tên người dùng cần lấy thông tin

        try {
            // Endpoint API v2 để lấy thông tin người dùng
            String endpoint = "https://api.twitter.com/2/users/by/username/" + username + "?user.fields=public_metrics";
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Hiển thị thông tin
                System.out.println("Response: " + response.toString());

                // Phân tích JSON (sử dụng thư viện JSON như org.json hoặc Gson để phân tích nếu cần)
                parseAndDisplayInfo(response.toString(), username);
            } else {
                System.out.println("Failed: HTTP error code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phân tích và hiển thị thông tin từ JSON trả về
    public static void parseAndDisplayInfo(String jsonResponse, String username) {
        try {
            // Dùng org.json để parse JSON
            org.json.JSONObject response = new org.json.JSONObject(jsonResponse);
            org.json.JSONObject userData = response.getJSONObject("data");
            String screenName = userData.getString("username");
            int followers = userData.getJSONObject("public_metrics").getInt("followers_count");
            int following = userData.getJSONObject("public_metrics").getInt("following_count");
            String profileLink = "https://twitter.com/" + screenName;

            // Hiển thị thông tin
            System.out.println("Username: @" + screenName);
            System.out.println("Followers: " + followers);
            System.out.println("Following: " + following);
            System.out.println("Profile Link: " + profileLink);
        } catch (Exception e) {
            System.out.println("Error parsing JSON response.");
            e.printStackTrace();
        }
    }
}
