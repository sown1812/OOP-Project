package pagerank;

import java.util.*;

public class PageRank {
    private static Graph graph;
    private static final double dampingFactor = 0.85; // Hệ số giảm d
    private static final int maxIterations = 100; // Số vòng lặp tối đa
    private static final double tolerance = 0.0001; // Sai số chấp nhận được

    public PageRank(Graph graph) {
        PageRank.graph = graph;
    }

    // Tính toán PageRank cho tất cả các user (KOL và người dùng thường)
    public Map<String, Double> calculatePageRank() {
        Map<String, Double> pageRank = new HashMap<>();
        Set<String> userList = graph.getAllUsers();
        int n = userList.size();

        // Khởi tạo điểm PageRank ban đầu
        for (String user : userList) {
            pageRank.put(user, 1.0 / n); // Điểm ban đầu của mỗi KOL là 1/N
        }

        // Chạy thuật toán PageRank
        for (int i = 0; i < maxIterations; i++) {
            Map<String, Double> newPageRank = new HashMap<>();
            double diff = 0.0;

            // Tính lại điểm PageRank cho từng user
            for (String user : userList) {
                double rankSum = 0.0;
                Set<Edge> followers = graph.getFollowers(user);

                // Tính điểm từ các mối quan hệ (follow, comment, retweet) có trọng số
                for (Edge edge : followers) {
                    String fromUser = edge.getFrom();
                    double weight = edge.getWeight();
                    if (edge.getType().equals("follow")) {
                        Set<String> userTweets = graph.getTweets(fromUser);
                        for (String tweet : userTweets) {
                            rankSum += pageRank.get(fromUser) * weight / userTweets.size();
                        }
                    }
                    // Comment và Retweet
                    if (edge.getType().equals("comment") || edge.getType().equals("retweet")) {
                        rankSum += pageRank.get(fromUser) * weight;
                    }
                }

                // Áp dụng công thức PageRank
                newPageRank.put(user, (1 - dampingFactor) / n + dampingFactor * rankSum);

                // Tính sai số giữa hai lần tính toán
                diff += Math.abs(newPageRank.get(user) - pageRank.get(user));
            }

            // Cập nhật lại điểm PageRank
            pageRank = newPageRank;

            // Dừng nếu sai số nhỏ hơn ngưỡng
            if (diff < tolerance) {
                break;
            }
        }

        return pageRank;
    }
}
