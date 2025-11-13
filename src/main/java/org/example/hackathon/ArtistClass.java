package org.example.hackathon;

//package com.example.myfirstjavafxproject.Workshop;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


import javax.sound.sampled.*;
import java.util.Random;

public class ArtistClass {

    public void start(Stage stage) {
        // === Background with stars ===
        Canvas canvas = new Canvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());

        drawStars(gc, 2020);


        canvas.widthProperty().addListener(e-> drawStars(gc, 2000));
        canvas.heightProperty().addListener((obs, o, n) -> drawStars(gc, 1000));


        // === Title Label ===
        Label title = new Label("Menu");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 63px; -fx-font-weight: bold;");

        // === Buttons ===
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 12px;");

        Label blank = new Label("");




        // === Layout ===
        VBox vbox = new VBox(20, title, startButton,blank, volumeLabel, sliderBox);
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(canvas, vbox);
        root.setStyle("-fx-background-color: black;");


        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Game Menu");
        stage.setScene(scene);

        // Bind the scale of the VBox to the scene size
        vbox.scaleXProperty().bind(scene.widthProperty().divide(800));
        vbox.scaleYProperty().bind(scene.heightProperty().divide(600));

        stage.show();
    }

    public static void drawStars(GraphicsContext gc, int count) {
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
}

