package org.example.hackathon;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.HashMap;
import java.util.Map;

public class GameInterface {

    // ROOT LAYOUT
    private BorderPane root = new BorderPane();

    // LEFT SIDE
    private VBox leftPanel;
    private Pane mapPane;
    private VBox resourcePanel;
    private Map<String, ProgressBar> resourceBars = new HashMap<>();

    // CENTER CHAT
    private VBox chatContainer = new VBox(10);
    private ScrollPane chatScroll = new ScrollPane(chatContainer);

    // CHOICE BUTTONS
    private VBox choicesBox = new VBox(10);

    // -------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------
    public GameInterface(Stage stage) {

        // Background
        root.setStyle("-fx-background-color: black;");

        setupLeftPanel();
        setupChatPanel();

        // LEFT = map + resources
        root.setLeft(leftPanel);

        // --- NEW LAYOUT FOR RIGHT SIDE ---
        VBox rightSide = new VBox();
        rightSide.setSpacing(10);

// chatScroll grows
        VBox.setVgrow(chatScroll, Priority.ALWAYS);

// style optional
        rightSide.setPadding(new Insets(15));

// add chat (top) and choices (bottom)
        rightSide.getChildren().addAll(chatScroll, choicesBox);

// put VBox into center of BorderPane
        root.setCenter(rightSide);

    }

    // -------------------------------------------------------------
    // LEFT PANEL SETUP
    // -------------------------------------------------------------
    private void setupLeftPanel() {

        leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setPrefWidth(350);

        // MAP
        mapPane = new Pane();
        mapPane.setPrefHeight(350);
        mapPane.setStyle("-fx-background-color: #111; -fx-border-color: #444; -fx-border-width: 2;");

        try {
            Image mapImg = new Image(getClass().getResourceAsStream("/org/example/hackathon/mars_base_map.png"));
            ImageView mapView = new ImageView(mapImg);
            mapView.setFitWidth(330);
            mapView.setPreserveRatio(true);
            mapPane.getChildren().add(mapView);
        } catch (Exception e) {
            Label fail = new Label("MAP NOT FOUND");
            fail.setTextFill(Color.RED);
            mapPane.getChildren().add(fail);
        }

        // RESOURCES
        resourcePanel = new VBox(10);
        resourcePanel.setPadding(new Insets(10));
        resourcePanel.setStyle("-fx-background-color: #1b1b1b; -fx-border-color: #444; -fx-border-width: 2;");

        Label title = new Label("RESOURCES");
        title.setFont(Font.font("Consolas", 18));
        title.setTextFill(Color.WHITE);
        resourcePanel.getChildren().add(title);

        addResource("Oxygen", Color.LIGHTBLUE);
        addResource("Food", Color.ORANGE);
        addResource("Morale", Color.GOLD);
        addResource("Signal Strength", Color.LIMEGREEN);

        leftPanel.getChildren().addAll(mapPane, resourcePanel);
    }

    private void addResource(String name, Color color) {
        Label label = new Label(name);
        label.setTextFill(Color.WHITE);

        ProgressBar bar = new ProgressBar(1.0);
        bar.setPrefWidth(300);
        bar.setStyle("-fx-accent: " + toWebColor(color));

        resourceBars.put(name, bar);

        resourcePanel.getChildren().addAll(label, bar);

        startOxygenDrain(0.001); // decrease 1% per second

    }

    public void startOxygenDrain(double drainPerSecond) {
        ProgressBar oxygenBar = resourceBars.get("Oxygen");
        if (oxygenBar == null) return;

        Timeline oxygenTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    double newValue = oxygenBar.getProgress() - drainPerSecond;
                    oxygenBar.setProgress(Math.max(newValue, 0)); // prevent going below 0
                })
        );
        oxygenTimeline.setCycleCount(Timeline.INDEFINITE);
        oxygenTimeline.play();
    }



    private void setupChatPanel() {
        chatContainer.setPadding(new Insets(20));
        chatScroll.setFitToWidth(true);
        chatScroll.setStyle("-fx-background: #000; -fx-border-color: #222;");
    }

    // -------------------------------------------------------------
    // CHAT MESSAGE DISPLAY
    // -------------------------------------------------------------
    public void addChatMessage(String text, boolean leftSide) {
        Label message = new Label(text);
        message.setTextFill(Color.WHITE);
        message.setWrapText(true);
        message.setMaxWidth(550);
        message.setPadding(new Insets(8, 12, 8, 12));
        message.setFont(Font.font("Consolas", 16));

        // bubble color
        message.setStyle(leftSide
                ? "-fx-background-color: #213547; -fx-background-radius: 12;"
                : "-fx-background-color: #1a472a; -fx-background-radius: 12;");

        HBox wrapper = new HBox(message);
        wrapper.setAlignment(leftSide ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        chatContainer.getChildren().add(wrapper);

        chatScroll.layout();
        chatScroll.setVvalue(1.0);
    }

    // -------------------------------------------------------------
    // CHOICE BUTTONS
    // -------------------------------------------------------------
    public void showChoices(java.util.List<Choice> choices, java.util.function.Consumer<Choice> callback) {

        choicesBox.getChildren().clear();
        choicesBox.setAlignment(Pos.BOTTOM_RIGHT);
        choicesBox.setSpacing(15);

        for (Choice c : choices) {

            Button b = new Button(c.text);

            b.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-background-color: rgba(255,255,255,0.15);
            -fx-text-fill: white;
            -fx-padding: 14px 30px;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0.5, 0, 2);
            """);

            // Hover effect to make it even clearer
            b.setOnMouseEntered(e -> b.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-background-color: rgba(255,255,255,0.35);
            -fx-text-fill: white;
            -fx-padding: 14px 30px;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 6, 0.5, 0, 2);
            """));

            b.setOnMouseExited(e -> b.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-background-color: rgba(255,255,255,0.15);
            -fx-text-fill: white;
            -fx-padding: 14px 30px;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0.5, 0, 2);
            """));

            b.setOnAction(e -> callback.accept(c));
            choicesBox.getChildren().add(b);
        }
    }

    public void clearChoices() {
        choicesBox.getChildren().clear();
    }

    // -------------------------------------------------------------
    // MAP + RESOURCES HELPERS
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // ROOT ACCESS
    // -------------------------------------------------------------
    public BorderPane getRoot() {
        return root;
    }
}
