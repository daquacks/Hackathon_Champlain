package org.example.hackathon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox; // Import VBox
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
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
    // Added a shorter pause for when the second line appears under the first
    private static final Duration SHORT_PAUSE = Duration.seconds(1.5);

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Mars 2035: The Distress Call");
        primaryStage.setScene(scene);
        primaryStage.show();

        playIntroAnimation(root);
    }

    private void playIntroAnimation(StackPane root) {
        SequentialTransition masterSequence = new SequentialTransition();

        // --- Part 1: Animate lines 0 and 1 normally ---
        // Loop now only goes up to i < 2
        for (int i = 0; i < 2; i++) {
            Label label = createStyledLabel(INTRO_TEXT[i]);
            label.setOpacity(0);
            root.getChildren().add(label);

            SequentialTransition lineSequence = new SequentialTransition(
                    createFadeIn(label),
                    new PauseTransition(DISPLAY_DURATION),
                    createFadeOut(label)
            );
            masterSequence.getChildren().add(lineSequence);
            masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));
        }

        // --- Part 2: Special handling for lines 2 and 3 ---
        Label label2 = createStyledLabel(INTRO_TEXT[2]);
        Label label3 = createStyledLabel(INTRO_TEXT[3]);
        label2.setOpacity(0);
        label3.setOpacity(0);

        // Use a VBox to stack them vertically
        VBox distressCallBox = new VBox(5); // 5px spacing
        distressCallBox.setAlignment(Pos.CENTER);
        distressCallBox.getChildren().addAll(label2, label3);
        root.getChildren().add(distressCallBox);

        // Create a parallel transition to fade them out *together*
        ParallelTransition fadeOutBoth = new ParallelTransition(
                createFadeOut(label2),
                createFadeOut(label3)
        );

        // Create a new sequence for this specific part:
        // 1. Fade in line 2
        // 2. Pause
        // 3. Fade in line 3 (while line 2 is still visible)
        // 4. Pause (while both are visible)
        // 5. Fade out both
        SequentialTransition distressSequence = new SequentialTransition(
                createFadeIn(label2),
                new PauseTransition(SHORT_PAUSE), // Shorter pause before line 3 appears
                createFadeIn(label3),
                new PauseTransition(DISPLAY_DURATION),
                fadeOutBoth
        );
        masterSequence.getChildren().add(distressSequence);
        masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));


        // --- Part 3: Special handling for the "Choice" (lines 4 and 5) ---
        Label label4 = createStyledLabel(INTRO_TEXT[4]);
        Label label5 = createStyledLabel(INTRO_TEXT[5]);
        label4.setOpacity(0);
        label5.setOpacity(0);

        VBox choiceBox = new VBox(10); // 10px spacing
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.getChildren().addAll(label4, label5);
        root.getChildren().add(choiceBox);

        // Fade *in* both choice lines together
        ParallelTransition fadeInChoice = new ParallelTransition(
                createFadeIn(label4),
                createFadeIn(label5)
        );

        // Fade *out* both choice lines together
        ParallelTransition fadeOutChoice = new ParallelTransition(
                createFadeOut(label4),
                createFadeOut(label5)
        );

        // Sequence for the choice
        SequentialTransition choiceSequence = new SequentialTransition(
                fadeInChoice,
                new PauseTransition(DISPLAY_DURATION),
                fadeOutChoice
        );
        masterSequence.getChildren().add(choiceSequence);


        // --- Part 4: OnFinished handler (unchanged) ---
        masterSequence.setOnFinished(event -> {
            System.out.println("Intro animation finished!");

            Label finishedLabel = createStyledLabel("Press Any Key to Start Game");
            finishedLabel.setOpacity(0);
            root.getChildren().clear(); // Clear previous labels
            root.getChildren().add(finishedLabel);

            FadeTransition finalFadeIn = createFadeIn(finishedLabel);
            finalFadeIn.play();

            //For background
            int[] r = {0};
            int[] g = {0};
            int[] b = {0};
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.001), e -> {
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

            // Add an event listener to transition to the next scene on key press
            root.setOnKeyPressed(e -> {
                System.out.println("Key pressed, starting game...");
                timeline.play();
                // TODO: Add code to switch to your main game scene here
            });
            root.setFocusTraversable(true); // Make sure the root can receive key events
            root.requestFocus(); // Give focus to the root
        });

        masterSequence.play();
    }

    // Helper method for consistent styling
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);
        label.setMaxWidth(800);
        return label;
    }

    // Helper method to create a standard fade-in
    private FadeTransition createFadeIn(Label label) {
        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, label);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        return fadeIn;
    }

    // Helper method to create a standard fade-out
    private FadeTransition createFadeOut(Label label) {
        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        return fadeOut;
    }

    public static void main(String[] args) {
        launch(args);
    }
}