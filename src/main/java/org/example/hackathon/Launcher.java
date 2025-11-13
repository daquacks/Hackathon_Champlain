package org.example.hackathon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas; // --- ADDED ---
import javafx.scene.canvas.GraphicsContext; // --- ADDED ---
import javafx.scene.control.Button; // --- ADDED ---
import javafx.scene.control.Label;
import javafx.scene.control.Slider; // --- ADDED ---
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle; // --- ADDED ---
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.HBox;

import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Random; // --- ADDED ---

public class Launcher extends Application {

    // ... (INTRO_TEXT array and constants remain unchanged) ...
    private static final String[] INTRO_TEXT = {
            "In 2030, humans managed to reach Mars.",
            "The current year is 2035, and you are working as a radio communicator.",
            "Suddenly, you receive a distress call,",
            "And you must help a human reach the base on Mars safely without dying.",
            "Will you get your head in the game and fight for the life of a stranger?",
            "Or will you be a coward and let him die?"
    };

    private static final Duration FADE_DURATION = Duration.seconds(2.5);
    private static final Duration DISPLAY_DURATION = Duration.seconds(2.0);
    private static final Duration PAUSE_BETWEEN_LINES = Duration.seconds(1.0);
    private static final Duration SHORT_PAUSE = Duration.seconds(1.5);
    private static final Duration TYPING_SPEED_PER_CHAR = Duration.millis(30);

    private Map<String, Dialogue> dialogues;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        root.setAlignment(Pos.CENTER);

        // --- Use 1280x720 for a more standard game resolution ---
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Mars 2035: The Distress Call");
        primaryStage.setScene(scene);
        primaryStage.show();

        playIntroAnimation(root, primaryStage);
    }

    // simple dialog presenter; replace PauseTransition with your typing animation if available
    private void showDialogue(StackPane root, String id, Stage primaryStage) {
        Dialogue d = dialogues == null ? null : dialogues.get(id);
        if (d == null) return;

        VBox container = new VBox(12);
        container.setMaxWidth(900);
        container.setStyle("-fx-padding: 20;");
        root.getChildren().setAll(container);

        SequentialTransition seq = new SequentialTransition();
        for (String line : d.lines) {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.6));
            pause.setOnFinished(e -> {
                Label lbl = new Label(line);
                lbl.setWrapText(true);
                lbl.setMaxWidth(800);
                container.getChildren().add(lbl);
            });
            seq.getChildren().add(pause);
        }

        seq.setOnFinished(e -> {
            if (d.choices == null || d.choices.isEmpty()) {
                // no choices -> continue (example: start the game)
                launchGame(primaryStage);
                return;
            }
            HBox choicesBox = new HBox(10);
            for (var c : d.choices) {
                Button b = new Button(c.text);
                b.setOnAction(evt -> {
                    if (c.nextId == null || c.nextId.isEmpty()) launchGame(primaryStage);
                    else showDialogue(root, c.nextId, primaryStage);
                });
                choicesBox.getChildren().add(b);
            }
            container.getChildren().add(choicesBox);
        });

        seq.play();
    }

    private void playIntroAnimation(StackPane root, Stage primaryStage) {
        // ... (All of Part 1, 2, 3 remains unchanged) ...
        SequentialTransition masterSequence = new SequentialTransition();

        // --- Part 1: Animate lines 0 and 1 normally ---
        for (int i = 0; i < 2; i++) {
            Label label = createStyledLabel(INTRO_TEXT[i]);
            root.getChildren().add(label);
            SequentialTransition lineSequence = new SequentialTransition(
                    createTypingAnimation(label, INTRO_TEXT[i]),
                    new PauseTransition(DISPLAY_DURATION),
                    createFadeOut(label)
            );
            masterSequence.getChildren().add(lineSequence);
            masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));
        }

        // --- Part 2: Special handling for lines 2 and 3 ---
        Label label2 = createStyledLabel(INTRO_TEXT[2]);
        Label label3 = createStyledLabel(INTRO_TEXT[3]);
        VBox distressCallBox = new VBox(5);
        distressCallBox.setAlignment(Pos.CENTER);
        distressCallBox.getChildren().addAll(label2, label3);
        root.getChildren().add(distressCallBox);
        ParallelTransition fadeOutBoth = new ParallelTransition(
                createFadeOut(label2), createFadeOut(label3)
        );
        SequentialTransition distressSequence = new SequentialTransition(
                createTypingAnimation(label2, INTRO_TEXT[2]),
                new PauseTransition(SHORT_PAUSE),
                createTypingAnimation(label3, INTRO_TEXT[3]),
                new PauseTransition(DISPLAY_DURATION),
                fadeOutBoth
        );
        masterSequence.getChildren().add(distressSequence);
        masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));


        // --- Part 3: Special handling for the "Choice" (lines 4 and 5) ---
        Label label4 = createStyledLabel(INTRO_TEXT[4]);
        Label label5 = createStyledLabel(INTRO_TEXT[5]);
        VBox choiceBox = new VBox(10);
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.getChildren().addAll(label4, label5);
        root.getChildren().add(choiceBox);
        FadeTransition fadeOut4 = createFadeOut(label4);
        FadeTransition fadeOut5 = createFadeOut(label5);
        Animation shakeAnimation = createShake(choiceBox);
        ParallelTransition fadeOutAndShake = new ParallelTransition(
                fadeOut4, fadeOut5, shakeAnimation
        );
        SequentialTransition choiceSequence = new SequentialTransition(
                createTypingAnimation(label4, INTRO_TEXT[4]),
                new PauseTransition(SHORT_PAUSE),
                createTypingAnimation(label5, INTRO_TEXT[5]),
                new PauseTransition(DISPLAY_DURATION),
                fadeOutAndShake
        );
        masterSequence.getChildren().add(choiceSequence);
        masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));

        // --- Part 4: OnFinished handler (MODIFIED) ---
        masterSequence.setOnFinished(event -> {
            System.out.println("Intro animation finished!");

            String finalMessage = "Press Any Key to Continue"; // Changed text
            Label finishedLabel = createStyledLabel(finalMessage);
            root.getChildren().clear(); // Clear previous labels
            root.getChildren().add(finishedLabel);

            Animation finalTyping = createTypingAnimation(finishedLabel, finalMessage);
            finalTyping.play();

            // --- REMOVED the background timeline, as it's not needed for the menu ---

            // --- MODIFIED KEY PRESS ---
            // This now triggers the flash-bang and loads the menu
            root.setOnKeyPressed(e -> {
                System.out.println("Key pressed, showing main menu...");

                // Call the new method to show the menu with a flash
                showTitleMenu(primaryStage);

            });
            root.setFocusTraversable(true); // Make sure the root can receive key events
            root.requestFocus(); // Give focus to the root
        });

        masterSequence.play();
    }

    // --- NEW METHOD: Replaces MenuClass.start() ---
    private void showTitleMenu(Stage primaryStage) {

        // === 1. Build the Menu Scene (logic from MenuClass) ===
        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawStars(gc, 20000); // Draw initial stars

        // Make stars redraw if window is resized
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());
        canvas.widthProperty().addListener((obs, o, n) -> drawStars(gc, 20000));
        canvas.heightProperty().addListener((obs, o, n) -> drawStars(gc, 20000));

        Label title = new Label("RED SIGNAL"); // Changed title
        title.setTextFill(Color.WHITE);
        // Use a more thematic font style
        title.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 100px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.8), 10, 0.5, 0, 0);");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';");
        // Add hover effect
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));

        Button configurations = new Button("Configurations");
        configurations.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';");
        // Add hover effect
        configurations.setOnMouseEntered(e -> configurations.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));
        configurations.setOnMouseExited(e -> configurations.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));

        // --- This is where the GameInterface is now launched ---
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked, loading game...");
            launchGame(primaryStage); // Call helper to launch main game
        });



        // --- This is the how to access the configurations menu
        configurations.setOnAction(e -> {
            ; // Call helper to launch main game
        });

        VBox vbox = new VBox(20, title, startButton, configurations);
        vbox.setAlignment(Pos.CENTER);

        StackPane menuRoot = new StackPane(canvas, vbox);
        menuRoot.setStyle("-fx-background-color: black;");
        Animations.flashbang(primaryStage, menuRoot);
    }

    // --- NEW HELPER METHOD ---
    // This contains the logic from your old setOnKeyPressed handler
    private void launchGame(Stage primaryStage) {
        GameInterface mainUI = new GameInterface(primaryStage);
        Animations.flashbang(primaryStage, mainUI.getRoot());
        mainUI.addChatMessage("Incoming transmission detected...", true);
        mainUI.addChatMessage("Commander Hale: This is Hale... is anyone reading me?", true);
        mainUI.addChatMessage("Control Center: We read you, Commander. What's your situation?", false);

        Timeline resourceTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), ev -> mainUI.updateResource("Oxygen", 0.85)),
                new KeyFrame(Duration.seconds(4), ev -> mainUI.updateResource("Morale", 0.75)),
                new KeyFrame(Duration.seconds(6), ev -> mainUI.updateResource("Connection", 0.65)),
                new KeyFrame(Duration.seconds(8), ev -> mainUI.updateResource("Food Supply", 0.5))
        );
        resourceTimeline.play();

        Timeline chatTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), ev -> mainUI.addChatMessage("Commander Hale: Oxygen levels stable, but I’ve lost visual contact with base.", true)),
                new KeyFrame(Duration.seconds(6), ev -> mainUI.addChatMessage("Control Center: Stay calm. Can you locate any landmarks?", false)),
                new KeyFrame(Duration.seconds(9), ev -> mainUI.addChatMessage("Commander Hale: There’s a ridge to the north... adding it to the map.", true)),
                new KeyFrame(Duration.seconds(12), ev -> mainUI.addMapLandmark("Delta Crater", 250, 140)),
                new KeyFrame(Duration.seconds(15), ev -> mainUI.addChatMessage("Control Center: Go fuck yourself, Hale.", false)),
                new KeyFrame(Duration.seconds(18), ev -> mainUI.addChatMessage("Commander Hale: :(", true)),
                new KeyFrame(Duration.seconds(21), ev -> mainUI.addChatMessage("Alien: hahaha that was actually funny man", true))
        );
        chatTimeline.play();
    }

    // --- NEW METHOD: Copied from MenuClass ---
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

    // ... (createStyledLabel, createTypingAnimation, createFadeOut, createShake, main methods are unchanged) ...

    // Helper method for consistent styling
    private Label createStyledLabel(String text) {
        Label label = new Label("");
        Font retroFont = null;
        try {
            // NOTE: Make sure your font file is named this or fix the path!
            retroFont = Font.loadFont(getClass().getResourceAsStream("/fonts/digital-7 (mono).ttf"), 36);
        } catch (Exception e) {
            System.err.println("Could not load font! Using default. Error: " + e.getMessage());
        }
        if (retroFont != null) {
            label.setFont(retroFont);
        } else {
            label.setFont(Font.font("Arial", FontWeight.BOLD, 36)); // Fallback
        }
        label.setTextFill(Color.color(0.0588235,0.2666666,0.0588235));
        label.setWrapText(true);
        label.setMaxWidth(800);
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    private Animation createTypingAnimation(Label label, String fullText) {
        final ObjectProperty<Integer> letterCount = new SimpleObjectProperty<>(0);
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            if (letterCount.get() <= 0) {
                return "";
            }
            return fullText.substring(0, Math.min(letterCount.get(), fullText.length()));
        }, letterCount));
        Timeline timeline = new Timeline();
        Duration duration = TYPING_SPEED_PER_CHAR.multiply(fullText.length());
        timeline.getKeyFrames().add(
                new KeyFrame(duration, new KeyValue(letterCount, fullText.length(), Interpolator.LINEAR))
        );
        return timeline;
    }

    private FadeTransition createFadeOut(Label label) {
        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        return fadeOut;
    }

    private Animation createShake(Node node) {
        Timeline timeline = new Timeline();
        double cycleDuration = 75; // 75 milliseconds
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(cycleDuration * 0.25), new KeyValue(node.translateXProperty(), -8)),
                new KeyFrame(Duration.millis(cycleDuration * 0.75), new KeyValue(node.translateXProperty(), 8)),
                new KeyFrame(Duration.millis(cycleDuration), new KeyValue(node.translateXProperty(), 0))
        );
        int shakeCycles = (int) (FADE_DURATION.toMillis() / cycleDuration);
        timeline.setCycleCount(shakeCycles);
        return timeline;
    }

    public static void main(String[] args) {
        launch(args);
    }
}