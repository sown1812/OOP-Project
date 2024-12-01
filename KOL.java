package com.example.project1.OOP;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Cập nhật class KOL để thêm thuộc tính following
public class KOL {
    private String username;
    private int followers;
    private String link;
    private int ranking;
    private int following; // Thêm thuộc tính following

    // Constructor
    public KOL(String username, int followers, String link, int ranking, int following) {
        this.username = username;
        this.followers = followers;
        this.link = link;
        this.ranking = ranking;
        this.following = following; // Khởi tạo thuộc tính following
    }

    // Getter và Setter cho following
    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    // Các phương thức getter và setter khác...
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    // Property getter cho TableView
    public IntegerProperty followersProperty() {
        return new SimpleIntegerProperty(followers);
    }

    public StringProperty usernameProperty() {
        return new SimpleStringProperty(username);
    }

    public StringProperty linkProperty() {
        return new SimpleStringProperty(link);
    }

    public IntegerProperty rankingProperty() {
        return new SimpleIntegerProperty(ranking);
    }

    // Property getter cho following
    public IntegerProperty followingProperty() {
        return new SimpleIntegerProperty(following);
    }
}
