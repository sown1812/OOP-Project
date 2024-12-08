package pagerank;

// Lớp đại diện cho một cạnh trong đồ thị
public class Edge {
    private String from;       // Đỉnh xuất phát
    private String to;         // Đỉnh đích
    private double weight;     // Trọng số của cạnh
    private String type;       // Loại mối quan hệ: "tweet", "comment", "retweet", "follow"

    // Constructor
    public Edge(String from, String to, double weight, String type) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.type = type;
    }

    // Getter và Setter
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
