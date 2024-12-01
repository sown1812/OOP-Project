package com.example.project1.OOP;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;

public class TwitterKOLSearchApp extends Application {
    // Dữ liệu cho bảng
    private ObservableList<KOL> kolData = FXCollections.observableArrayList();

    // Configuration for API access
    private static final String CONFIG_FILE = "twitter_api_config.properties";
    private Properties apiConfig;

    @Override
    public void start(Stage primaryStage) {
        // Load API configuration
        loadApiConfiguration();

        // Tiêu đề và phần nhập liệu
        Label titleLabel = new Label("Twitter KOL Search");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter keyword or topic");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        // Nút tìm kiếm
        Button searchButton = new Button("Search KOLs");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        searchButton.setPadding(new Insets(10));

        // Tạo TableView để hiển thị dữ liệu KOL
        TableView<KOL> kolTable = new TableView<>();

        // Cột Ranking
        TableColumn<KOL, Integer> rankingColumn = new TableColumn<>("Ranking");
        rankingColumn.setCellValueFactory(cellData -> cellData.getValue().rankingProperty().asObject());

        // Cột Username
        TableColumn<KOL, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        // Cột Lượng Follow
        TableColumn<KOL, Integer> followColumn = new TableColumn<>("Followers");
        followColumn.setCellValueFactory(cellData -> cellData.getValue().followersProperty().asObject());

        // Cột Following
        TableColumn<KOL, Integer> followingColumn = new TableColumn<>("Following");
        followingColumn.setCellValueFactory(cellData -> cellData.getValue().followingProperty().asObject());

        // Cột Link tới trang cá nhân
        TableColumn<KOL, String> linkColumn = new TableColumn<>("Profile Link");
        linkColumn.setCellValueFactory(cellData -> cellData.getValue().linkProperty());
        linkColumn.setCellFactory(new Callback<TableColumn<KOL, String>, TableCell<KOL, String>>() {
            @Override
            public TableCell<KOL, String> call(TableColumn<KOL, String> param) {
                return new TableCell<KOL, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Hyperlink hyperlink = new Hyperlink(item);
                            hyperlink.setOnAction(e -> {
                                getHostServices().showDocument(item);
                            });
                            setGraphic(hyperlink);
                        }
                    }
                };
            }
        });

        // Thêm các cột vào TableView
        kolTable.getColumns().addAll(usernameColumn, followColumn, followingColumn, linkColumn, rankingColumn);

        // Ẩn bảng khi bắt đầu
        kolTable.setVisible(false);

        // Xử lý sự kiện nút tìm kiếm
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                // Xóa dữ liệu cũ
                kolData.clear();

                // Thực hiện tìm kiếm và cập nhật bảng
                searchKOLsByKeyword(keyword, kolTable);
            } else {
                showAlert("Please enter a keyword or topic.");
            }
        });

        // Tab cho bảng và đồ thị
        TabPane tabPane = new TabPane();

        // Tab cho bảng
        Tab tableTab = new Tab("KOL Information");
        tableTab.setClosable(false);
        VBox tableLayout = new VBox(10, titleLabel, searchField, searchButton, kolTable);
        tableLayout.setPadding(new Insets(20));
        tableTab.setContent(tableLayout);

        // Tab cho đồ thị
        Tab graphTab = new Tab("KOL Graph");
        graphTab.setClosable(false);
        VBox graphLayout = createGraphTab(kolData);
        graphTab.setContent(graphLayout);

        tabPane.getTabs().addAll(tableTab, graphTab);

        // Thêm icon vào stage
        Image icon = new Image(getClass().getResourceAsStream("/images.png"));
        primaryStage.getIcons().add(icon);

        // Đặt tất cả trong một Scene và hiển thị
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Twitter KOL Search");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Load API configuration from properties file
    private void loadApiConfiguration() {
        apiConfig = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                // Create default config file if it doesn't exist
                createDefaultConfigFile();
                input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            }

            if (input != null) {
                apiConfig.load(input);
            } else {
                throw new FileNotFoundException("Configuration file not found");
            }
        } catch (IOException ex) {
            showAlert("Error loading API configuration: " + ex.getMessage());
            apiConfig = new Properties();
        }
    }

    // Create a default configuration file
    private void createDefaultConfigFile() {
        try {
            // Determine the correct path to save the file
            File configFile = new File(System.getProperty("user.home"), CONFIG_FILE);

            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("# Twitter API Configuration\n");
                writer.write("# Replace these with your actual API credentials\n");
                writer.write("bearer_token=YOUR_BEARER_TOKEN_HERE\n");
                writer.write("api_key=YOUR_API_KEY_HERE\n");
                writer.write("api_key_secret=YOUR_API_KEY_SECRET_HERE\n");
            }

            showAlert("Default configuration file created at: " + configFile.getAbsolutePath() +
                    "\nPlease edit the file with your Twitter API credentials.");
        } catch (IOException ex) {
            showAlert("Error creating default configuration file: " + ex.getMessage());
        }
    }

    // Phương thức tìm kiếm KOLs theo từ khóa
    private void searchKOLsByKeyword(String keyword, TableView<KOL> kolTable) {
        new Thread(() -> {
            try {
                // Retrieve bearer token from config
                String bearerToken = apiConfig.getProperty("bearer_token", "");
                if (bearerToken.isEmpty() || bearerToken.equals("YOUR_BEARER_TOKEN_HERE")) {
                    Platform.runLater(() -> {
                        showAlert("Please configure your Twitter API Bearer Token in the config file.");
                        return;
                    });
                    return;
                }

                // Mã hóa từ khóa để sử dụng trong URL
                String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());

                // Endpoint API v2 để tìm kiếm người dùng
                String endpoint = "https://api.twitter.com/2/users/search?q=" + encodedKeyword + "&user.fields=public_metrics";
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

                    // Parse và hiển thị thông tin
                    Platform.runLater(() -> parseAndDisplayUsersInfo(response.toString(), kolTable));
                } else {
                    Platform.runLater(() -> {
                        // More detailed error handling
                        String errorMessage = "Failed to fetch user data. Error code: " + responseCode;
                        if (responseCode == 403) {
                            errorMessage += "\nPossible reasons:\n" +
                                    "1. Invalid or expired Bearer Token\n" +
                                    "2. Insufficient API permissions\n" +
                                    "3. Account issues with Twitter Developer Portal";
                        } else if (responseCode == 401) {
                            errorMessage += "\nUnauthorized: Check your authentication credentials";
                        }
                        showAlert(errorMessage);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    e.printStackTrace();
                    showAlert("Error fetching Twitter user data: " + e.getMessage());
                });
            }
        }).start();
    }

    // Phân tích và hiển thị thông tin từ JSON trả về
    private void parseAndDisplayUsersInfo(String jsonResponse, TableView<KOL> kolTable) {
        try {
            // Dùng org.json để parse JSON
            JSONObject response = new JSONObject(jsonResponse);

            // Check if data exists in the response
            if (!response.has("data")) {
                showAlert("No users found for the given keyword.");
                return;
            }

            JSONArray usersData = response.getJSONArray("data");

            // Danh sách để lưu các KOL
            List<KOL> kols = new ArrayList<>();

            // Lặp qua từng người dùng
            for (int i = 0; i < usersData.length(); i++) {
                JSONObject userData = usersData.getJSONObject(i);
                String screenName = userData.getString("username");

                // Safely retrieve public metrics
                JSONObject publicMetrics = userData.getJSONObject("public_metrics");
                int followers = publicMetrics.getInt("followers_count");
                int following = publicMetrics.getInt("following_count");
                String profileLink = "https://twitter.com/" + screenName;

                // Tạo đối tượng KOL mới và thêm vào danh sách
                KOL kol = new KOL("@" + screenName, followers, profileLink, i + 1, following);
                kols.add(kol);

                // Hiển thị thông tin
                System.out.println("Username: @" + screenName);
                System.out.println("Followers: " + followers);
                System.out.println("Following: " + following);
                System.out.println("Profile Link: " + profileLink);
            }

            // Cập nhật dữ liệu cho TableView
            kolData.addAll(kols);
            kolTable.setItems(kolData);
            kolTable.setVisible(true);

            // Nếu không tìm thấy người dùng nào
            if (kols.isEmpty()) {
                showAlert("No KOLs found for the given keyword.");
            }
        } catch (Exception e) {
            showAlert("Error parsing user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức tạo đồ thị với dữ liệu động
    private VBox createGraphTab(ObservableList<KOL> kolData) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Username");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Followers");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("KOL Followers");

        // Listener to update graph when KOL data changes
        kolData.addListener((javafx.collections.ListChangeListener.Change<? extends KOL> c) -> {
            Platform.runLater(() -> {
                // Clear existing data
                barChart.getData().clear();

                // Create new series
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Followers");

                // Add data from kolData
                for (KOL kol : kolData) {
                    series.getData().add(new XYChart.Data<>(kol.getUsername(), kol.getFollowers()));
                }

                // Add series to chart if not empty
                if (!series.getData().isEmpty()) {
                    barChart.getData().add(series);
                }
            });
        });

        VBox graphLayout = new VBox(10, barChart);
        graphLayout.setPadding(new Insets(20));
        return graphLayout;
    }

    // Hiển thị thông báo
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}