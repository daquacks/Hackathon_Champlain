package org.example.hackathon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Launcher extends Application {

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
    // New constant for typing speed (30ms per character)
    private static final Duration TYPING_SPEED_PER_CHAR = Duration.millis(30);

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Mars 2035: The Distress Call");
        primaryStage.setScene(scene);
        primaryStage.show();

        playIntroAnimation(root, primaryStage);
    }

    private void playIntroAnimation(StackPane root, Stage primaryStage) {
        SequentialTransition masterSequence = new SequentialTransition();

        // --- Part 1: Animate lines 0 and 1 normally ---
        for (int i = 0; i < 2; i++) {
            // We pass the full text to createStyledLabel so it can be empty initially
            Label label = createStyledLabel(INTRO_TEXT[i]);
            root.getChildren().add(label);

            SequentialTransition lineSequence = new SequentialTransition(
                    // Use new typing animation
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

        VBox distressCallBox = new VBox(5); // 5px spacing
        distressCallBox.setAlignment(Pos.CENTER);
        distressCallBox.getChildren().addAll(label2, label3);
        root.getChildren().add(distressCallBox);

        ParallelTransition fadeOutBoth = new ParallelTransition(
                createFadeOut(label2),
                createFadeOut(label3)
        );

        SequentialTransition distressSequence = new SequentialTransition(
                // Use new typing animation
                createTypingAnimation(label2, INTRO_TEXT[2]),
                new PauseTransition(SHORT_PAUSE),
                // Use new typing animation
                createTypingAnimation(label3, INTRO_TEXT[3]),
                new PauseTransition(DISPLAY_DURATION),
                fadeOutBoth
        );
        masterSequence.getChildren().add(distressSequence);
        masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));


        // --- Part 3: Special handling for the "Choice" (lines 4 and 5) ---
        Label label4 = createStyledLabel(INTRO_TEXT[4]);
        Label label5 = createStyledLabel(INTRO_TEXT[5]);

        VBox choiceBox = new VBox(10); // 10px spacing
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.getChildren().addAll(label4, label5);
        root.getChildren().add(choiceBox);

        FadeTransition fadeOut4 = createFadeOut(label4);
        FadeTransition fadeOut5 = createFadeOut(label5);
        Animation shakeAnimation = createShake(choiceBox);

        ParallelTransition fadeOutAndShake = new ParallelTransition(
                fadeOut4,
                fadeOut5,
                shakeAnimation
        );

        SequentialTransition choiceSequence = new SequentialTransition(
                // Use new typing animation
                createTypingAnimation(label4, INTRO_TEXT[4]),
                new PauseTransition(SHORT_PAUSE),
                // Use new typing animation
                createTypingAnimation(label5, INTRO_TEXT[5]),
                new PauseTransition(DISPLAY_DURATION),
                fadeOutAndShake
        );

        masterSequence.getChildren().add(choiceSequence);
        masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));


        // --- Part 4: OnFinished handler ---
        masterSequence.setOnFinished(event -> {
            System.out.println("Intro animation finished!");

            String finalMessage = "Press Any Key to Start Game";
            Label finishedLabel = createStyledLabel(finalMessage);
            root.getChildren().clear(); // Clear previous labels
            root.getChildren().add(finishedLabel);

            // Use typing animation for the final label too
            Animation finalTyping = createTypingAnimation(finishedLabel, finalMessage);
            finalTyping.play();

            //For background
            int[] r = {0};
            int[] g = {0};
            int[] b = {0};
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.01), e -> {
                Color color = Color.rgb(r[0], g[0], b[0], 1.0);

                root.setBackground(new Background(
                        new BackgroundFill(color, null, null)));

                if (r[0] != 255) {
                    r[0]++;
                    g[0]++;
                    b[0]++;
                }
            }));

            timeline.setCycleCount(Timeline.INDEFINITE);

            root.setOnKeyPressed(e -> {
                System.out.println("Key pressed, starting game...");
                timeline.stop();
                GameInterface mainUI = new GameInterface(primaryStage);
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
            });
            root.setFocusTraversable(true); // Make sure the root can receive key events
            root.requestFocus(); // Give focus to the root
        });

        masterSequence.play();
    }

    // Helper method for consistent styling
    private Label createStyledLabel(String text) {
        // Label starts empty, will be filled by animation
        Label label = new Label("");
        Font retroFont = null;

        try {
            // Assumes font is renamed to "digital-7-mono.ttf" in resources/fonts/
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
        // This is the key for centering wrapped text
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    /**
     * Creates a typing animation for a Label.
     * @param label The label to animate.
     * @param fullText The full text to type out.
     * @return An Animation that types the text.
     */
    private Animation createTypingAnimation(Label label, String fullText) {
        // Use an ObjectProperty to animate the visible character count
        final ObjectProperty<Integer> letterCount = new SimpleObjectProperty<>(0);

        // Bind the label's text to a substring of the full text
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            if (letterCount.get() <= 0) {
                return "";
            }
            return fullText.substring(0, Math.min(letterCount.get(), fullText.length()));
        }, letterCount));

        // Create the timeline to animate the letterCount
        Timeline timeline = new Timeline();
        Duration duration = TYPING_SPEED_PER_CHAR.multiply(fullText.length());

        timeline.getKeyFrames().add(
                new KeyFrame(duration, new KeyValue(letterCount, fullText.length(), Interpolator.LINEAR))
        );

        return timeline;
    }

    // Helper method to create a standard fade-out (still needed)
    private FadeTransition createFadeOut(Label label) {
        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        return fadeOut;
    }

    // Shake animation method (unchanged)
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