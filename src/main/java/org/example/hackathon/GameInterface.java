package org.example.hackathon;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class GameInterface {

    // ... (UI Components and maps are unchanged) ...
    private BorderPane root;
    private VBox leftPanel;
    private Pane mapPane;
    private VBox resourcePanel;
    private ScrollPane chatScroll;
    private VBox chatBox;
    private Map<String, ProgressBar> resourceBars;
    private Map<String, Label> resourceLabels;


    // --- Constructor ---
    public GameInterface(Stage stage) {
        root = new BorderPane();
        root.setPrefSize(1280, 720); // Matched size to launcher

        setupLeftPanel();
        setupChatPanel();

        // Assemble layout
        root.setLeft(leftPanel);
        root.setCenter(chatScroll);

        Scene scene = new Scene(root);
        // Make sure game.css is in src/main/resources/styles/
        scene.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Red Signal: A Message from Mars");
        stage.show(); // Stage is already showing, this just sets the new scene
    }

    // -------------------------------------------------------------
    // LEFT PANEL SETUP (Map + Resources)
    // -------------------------------------------------------------
    private void setupLeftPanel() {
        leftPanel = new VBox();
        leftPanel.setPrefWidth(400);
        leftPanel.setSpacing(10);
        leftPanel.setPadding(new Insets(10));

        // Top: interactive map
        mapPane = new Pane();
        mapPane.setPrefHeight(400);
        mapPane.setStyle("-fx-background-color: #111820; -fx-border-color: #555; -fx-border-width: 2;");

        // --- IMAGE PATH FIXED ---
        // This path assumes 'mars_base_map.png' is in 'src/main/resources/'
        // If it's in 'src/main/resources/images/', use "/images/mars_base_map.png"
        Image marsImage = null;
        try {
            marsImage = new Image(getClass().getResourceAsStream("/org/example/hackathon/mars_base_map.png"));
        } catch (Exception e) {
            System.err.println("Could not load map image: /mars_base_map.png. " + e.getMessage());
        }

        if (marsImage != null) {
            ImageView marsMap = new ImageView(marsImage);
            marsMap.setFitWidth(380);
            marsMap.setPreserveRatio(true);
            mapPane.getChildren().add(marsMap);
        } else {
            // Placeholder if image fails to load
            Label noImage = new Label("Map Data Not Found");
            noImage.setTextFill(Color.RED);
            mapPane.getChildren().add(noImage);
        }
        // --- END OF FIX ---

        // Bottom: resources
        resourcePanel = new VBox(10);
        resourcePanel.setPadding(new Insets(10));
        resourcePanel.setAlignment(Pos.TOP_LEFT);
        resourcePanel.setStyle("-fx-background-color: #1b1b1b; -fx-border-color: #444; -fx-border-width: 2;");

        Label resTitle = new Label("RESOURCE STATUS");
        resTitle.setFont(Font.font("Consolas", 18));
        resTitle.setTextFill(Color.LIGHTGRAY);
        resourcePanel.getChildren().add(resTitle);

        // Initialize resource bars
        resourceBars = new HashMap<>();
        resourceLabels = new HashMap<>();

        addResource("Oxygen", Color.LIGHTBLUE);
        addResource("Morale", Color.GOLD);
        addResource("Connection", Color.LIMEGREEN);
        addResource("Food Supply", Color.ORANGE);

        leftPanel.getChildren().addAll(mapPane, resourcePanel);
    }

    // ... (Rest of GameInterface.java is unchanged) ...
    private void addResource(String name, Color color) {
        Label label = new Label(name);
        label.setTextFill(Color.WHITE);
        ProgressBar bar = new ProgressBar(1.0);
        bar.setPrefWidth(360);
        bar.setStyle("-fx-accent: " + toWebColor(color) + ";");
        resourcePanel.getChildren().addAll(label, bar);

        resourceBars.put(name, bar);
        resourceLabels.put(name, label);
    }

    private void setupChatPanel() {
        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setFillWidth(true);
        chatBox.setStyle("-fx-background-color: #0d0d0d;");

        chatScroll = new ScrollPane(chatBox);
        chatScroll.setFitToWidth(true);
        chatScroll.setStyle("-fx-background: #0d0d0d; -fx-border-color: #333;");
    }

    public void addChatMessage(String message, boolean fromAstronaut) {
        Label msg = new Label(message);
        msg.setWrapText(true);
        msg.setMaxWidth(500);
        msg.setPadding(new Insets(8, 12, 8, 12));
        msg.setFont(Font.font("Consolas", 14));
        msg.setTextFill(Color.WHITE);
        msg.setStyle(fromAstronaut
                ? "-fx-background-color: #293241; -fx-background-radius: 12;"
                : "-fx-background-color: #1b4332; -fx-background-radius: 12;");
        msg.setAlignment(fromAstronaut ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        HBox wrapper = new HBox(msg);
        wrapper.setAlignment(fromAstronaut ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        chatBox.getChildren().add(wrapper);
        chatScroll.setVvalue(1.0);
    }

    public void updateResource(String name, double value) {
        if (resourceBars.containsKey(name)) {
            resourceBars.get(name).setProgress(value);
        }
    }

    public void addMapLandmark(String name, double x, double y) {
        Circle marker = new Circle(x, y, 6, Color.LIMEGREEN);
        Tooltip.install(marker, new Tooltip(name));
        mapPane.getChildren().add(marker);
    }

    private String toWebColor(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    public BorderPane getRoot() {
        return root;
    }
}