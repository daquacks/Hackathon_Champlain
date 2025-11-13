package org.example.hackathon;

//package com.example.myfirstjavafxproject.Workshop;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;
import java.util.Random;

public class MenuClass extends Application {

    @Override
    public void start(Stage stage) {
        // === Background with stars ===
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawStars(gc, 20220);

        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        canvas.widthProperty().addListener((obs, o, n) -> drawStars(gc, 22200));
        canvas.heightProperty().addListener((obs, o, n) -> drawStars(gc, 10));


        // === Title Label ===
        Label title = new Label("Menu");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 123px; -fx-font-weight: bold;");

        // === Buttons ===
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px;");

        Label volumeLabel = new Label("Volume");
        volumeLabel.setTextFill(Color.WHITE);
        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setPrefWidth(200);

        // === Music (optional) ===
        String musicPath = "src/main/resources/music.mp3"; // Replace with your file
//        MediaPlayer mediaPlayer = null;
//        try {
//            Media music = new Media(new File(musicPath).toURI().toString());
//            mediaPlayer = new MediaPlayer(music);
//            mediaPlayer.setVolume(0.5);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//            mediaPlayer.play();
//
//            MediaPlayer finalMediaPlayer = mediaPlayer;
//            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//                finalMediaPlayer.setVolume(newVal.doubleValue());
//            });
//        } catch (Exception e) {
//            System.out.println("⚠️ Music file not found or could not be played.");
//        }

        // === Layout ===
        VBox vbox = new VBox(20, title, startButton, volumeLabel, volumeSlider);
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(canvas, vbox);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Game Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void drawStars(GraphicsContext gc, int count) {
        Random rand = new Random();

        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        for (int i = 0; i < count; i++) {
            double x = rand.nextDouble() * width;
            double y = rand.nextDouble() * height;
            double radius = rand.nextDouble() * 2;
            gc.fillOval(x, y, radius, radius);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

