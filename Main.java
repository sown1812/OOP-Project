package pagerank;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Đọc dữ liệu từ tệp
        Graph graph = new Graph();

        try (BufferedReader reader = new BufferedReader(new FileReader("E:\\Java_Project\\PagerankUpdate\\data\\testdemo.txt"))) {
            String line;
            boolean isUsersSection = false;
            boolean isTweetsSection = false;
            boolean isFollowRelationsSection = false;
            boolean isInteractionsSection = false;

            // Đọc tệp dòng theo dòng
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Bỏ qua các dòng trống hoặc dấu chú thích
                if (line.isEmpty() || line.startsWith("#")) {
                    if (line.startsWith("# Users")) {
                        isUsersSection = true;
                        isTweetsSection = false;
                        isFollowRelationsSection = false;
                        isInteractionsSection = false;
                    } else if (line.startsWith("# Tweets")) {
                        isUsersSection = false;
                        isTweetsSection = true;
                        isFollowRelationsSection = false;
                        isInteractionsSection = false;
                    } else if (line.startsWith("# Follow Relations")) {
                        isUsersSection = false;
                        isTweetsSection = false;
                        isFollowRelationsSection = true;
                        isInteractionsSection = false;
                    } else if (line.startsWith("# Interactions")) {
                        isUsersSection = false;
                        isTweetsSection = false;
                        isFollowRelationsSection = false;
                        isInteractionsSection = true;
                    }
                    continue;
                }

                // Xử lý các phần dữ liệu
                if (isUsersSection) {
                    // Thêm người dùng vào đồ thị
                    graph.addUser(line);
                } else if (isTweetsSection) {
                    // Thêm tweet vào đồ thị
                    String[] tweetData = line.split(",");
                    graph.addTweet(tweetData[0], tweetData[1]);
                } else if (isFollowRelationsSection) {
                    // Thêm mối quan hệ theo dõi vào đồ thị
                    String[] followData = line.split(",");
                    graph.addFollowRelation(followData[0], followData[1]);
                } else if (isInteractionsSection) {
                    // Thêm tương tác vào đồ thị
                    String[] interactionData = line.split(",");
                    graph.addInteraction(interactionData[0], interactionData[1], interactionData[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tính toán PageRank
        PageRank pageRank = new PageRank(graph);
        Map<String, Double> ranks = pageRank.calculatePageRank();

        // In kết quả
        System.out.println("PageRank results:");
        for (Map.Entry<String, Double> entry : ranks.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}