package pagerank;

import java.util.*;

// Lớp đại diện cho đồ thị
public class Graph {
    private final Map<String, Set<Edge>> adjList; // Danh sách các đỉnh và các cạnh của chúng
    private final Map<String, Set<String>> tweetLinks; // Lưu trữ các tweet của mỗi KOL

    public Graph() {
        adjList = new HashMap<>();
        tweetLinks = new HashMap<>();
    }

    // Thêm một KOL vào đồ thị
    public void addUser(String user) {
        adjList.putIfAbsent(user, new HashSet<>());
        tweetLinks.putIfAbsent(user, new HashSet<>());
    }

    // Thêm mối quan hệ theo dõi giữa hai user (cạnh KOL-KOL)
    public void addFollowRelation(String follower, String followee) {
        adjList.putIfAbsent(follower, new HashSet<>());
        adjList.get(follower).add(new Edge(follower, followee, 0.5, "follow"));
    }

    // Thêm tweet cho KOL (cạnh KOL -> Tweet)
    public void addTweet(String kol, String tweet) {
        tweetLinks.putIfAbsent(kol, new HashSet<>());
        tweetLinks.get(kol).add(tweet);
    }

    // Thêm mối quan hệ comment hoặc retweet từ User hoặc KOL tới tweet
    public void addInteraction(String user, String tweet, String interactionType) {
        double weight = interactionType.equals("retweet") ? 0.3 : 0.2;
        adjList.putIfAbsent(user, new HashSet<>());
        adjList.get(user).add(new Edge(user, tweet, weight, interactionType));
    }

    // Thêm mối quan hệ từ KOL tới KOL khác (cạnh KOL -> KOL)
    public void addFollowRelationKOL(String kol1, String kol2) {
        adjList.putIfAbsent(kol1, new HashSet<>());
        adjList.get(kol1).add(new Edge(kol1, kol2, 0.5, "follow"));
    }

    // Lấy các followers của một KOL
    public Set<Edge> getFollowers(String kol) {
        return adjList.getOrDefault(kol, Collections.emptySet());
    }

    // Lấy các tweet của KOL
    public Set<String> getTweets(String kol) {
        return tweetLinks.getOrDefault(kol, Collections.emptySet());
    }

    // Lấy tất cả các KOL trong đồ thị
    public Set<String> getAllUsers() {
        return adjList.keySet();
    }
}
