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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MenuClass extends Application {

    @Override
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

        Label volumeLabel = new Label("Volume");
        volumeLabel.setTextFill(Color.WHITE);
        volumeLabel.setStyle("""
    -fx-background-color: transparent;
    -fx-text-fill: white;
    
    
    -fx-font-size: 12px;
    -fx-font-weight: bold;
    -fx-padding: 5px 20px;
    -fx-alignment: center;
""");


        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setPrefWidth(250); // shorter width
        volumeSlider.setStyle("""
    -fx-control-inner-background: black;
    -fx-accent: white;
    -fx-pref-height: 6px;
    -fx-padding: 2px;
""");

        String musicPath = "/Users/nicolasbeauchemin/Documents/Hackathon_Champlain/src/main/resources/music/spaceMusic"; // full path to WAV

        try {
            File musicFile = new File(musicPath);
            if (musicFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // loop music
                clip.start();

                // Connect volume slider
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    float min = volumeControl.getMinimum(); // usually -80
                    float max = volumeControl.getMaximum(); // usually 6
                    float value = min + (max - min) * newVal.floatValue();
                    volumeControl.setValue(value);
                });

            } else {
                System.out.println("Music file not found: " + musicPath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        HBox sliderBox = new HBox(volumeSlider);
        sliderBox.setAlignment(Pos.CENTER);
        sliderBox.setPrefWidth(200);



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

