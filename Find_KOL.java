import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Find_KOL {

    private static final String BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAMMmxQEAAAAATlgRfQfrlW0hvOi9xv9qhZB9Opw%3DP1CoW468fKIwUXazKIEVMjyEf03nGbcA8Ash0cJtJd5Y380hC8";

    public static void main(String[] args) {
        String hashtag = "yourHashtag";

        String urlString = "https://api.twitter.com/2/tweets/search/recent?query=%23" + hashtag + "&max_results=10";

        try {
            // Tạo kết nối URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Cấu hình request
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");

            // Đọc kết quả từ API
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Phân tích kết quả JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray tweets = jsonResponse.getJSONArray("data");

                // Lấy thông tin các user từ author_id
                System.out.println("List of Users who used the hashtag:");
                for (int i = 0; i < tweets.length(); i++) {
                    JSONObject tweet = tweets.getJSONObject(i);
                    System.out.println("User ID: " + tweet.getString("author_id"));
                    System.out.println("Tweet: " + tweet.getString("text"));
                    System.out.println("-----");
                }
            } else {
                System.out.println("Failed to fetch tweets. HTTP Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
