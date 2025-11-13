package org.example.hackathon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color; // Import Color class
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

        private static final Duration FADE_DURATION = Duration.seconds(2.5); // Fade in/out duration
        private static final Duration DISPLAY_DURATION = Duration.seconds(2); // How long text stays fully visible
        private static final Duration PAUSE_BETWEEN_LINES = Duration.seconds(1); // Pause after a line fades out

        @Override
        public void start(Stage primaryStage) {
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: black;"); // Set background to black
            root.setAlignment(Pos.CENTER); // Center the text

            Scene scene = new Scene(root, 1000, 600); // Adjust scene size as needed
            primaryStage.setTitle("Mars 2035: The Distress Call");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Start the animation sequence
            playIntroAnimation(root);
        }

        private void playIntroAnimation(StackPane root) {
            SequentialTransition masterSequence = new SequentialTransition();

            for (int i = 0; i < 3; i++) {
                String lineText = INTRO_TEXT[i];
                Label label = createStyledLabel(lineText);
                label.setOpacity(0); // Start invisible
                root.getChildren().add(label); // Add label to the scene graph

                // Fade in transition
                FadeTransition fadeIn = new FadeTransition(FADE_DURATION, label);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                // Pause for display
                PauseTransition displayPause = new PauseTransition(DISPLAY_DURATION);

                // Fade out transition
                FadeTransition fadeOut = new FadeTransition(FADE_DURATION, label);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                // Sequence for one line: Fade In -> Display -> Fade Out
                SequentialTransition lineSequence = new SequentialTransition(fadeIn, displayPause, fadeOut);
                masterSequence.getChildren().add(lineSequence);

                // Add a pause between lines, but not after the very last line
                if (i < INTRO_TEXT.length - 1) {
                    masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));
                }
            }

            Label label1 = createStyledLabel(INTRO_TEXT[3]);
            label1.setOpacity(0); // Start invisible
            root.getChildren().add(label1);
            FadeTransition fadeIn1 = new FadeTransition(FADE_DURATION, label1);
            fadeIn1.setFromValue(0.0);
            fadeIn1.setToValue(1.0);
            PauseTransition displayPause = new PauseTransition(DISPLAY_DURATION);
            Label label2 = createStyledLabel(INTRO_TEXT[4]);
            label2.setOpacity(0);
            root.getChildren().add(label2);
            FadeTransition fadeIn2 = new FadeTransition(FADE_DURATION, label2);
            FadeTransition fadeOut1 = new FadeTransition(FADE_DURATION, label1);
            FadeTransition fadeOut2 = new FadeTransition(FADE_DURATION, label2);
            fadeOut1.setFromValue(1.0);
            fadeOut1.setToValue(0.0);
            fadeOut2.setFromValue(1.0);
            fadeOut2.setToValue(0.0);
            SequentialTransition lineSequence1 = new SequentialTransition(fadeIn1, displayPause, fadeOut1);
            SequentialTransition lineSequence2 = new SequentialTransition(fadeIn2, displayPause, fadeOut2);
            masterSequence.getChildren().addAll(lineSequence1, lineSequence2);


            // After all animations complete, you can transition to your main game scene
            masterSequence.setOnFinished(event -> {
                System.out.println("Intro animation finished!");
                // TODO: Load and display main game scene
                // For now, let's just clear the root or display a simple message
                // TODO
                Label finishedLabel = createStyledLabel("Press Any Key to Start Game");
                finishedLabel.setOpacity(0);
                root.getChildren().clear(); // Clear previous labels
                root.getChildren().add(finishedLabel);

                FadeTransition finalFadeIn = new FadeTransition(FADE_DURATION, finishedLabel);
                finalFadeIn.setFromValue(0.0);
                finalFadeIn.setToValue(1.0);
                finalFadeIn.play();

                //For background
                int [] r = {0};
                int [] g = {0};
                int [] b = {0};
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
                });
                root.setFocusTraversable(true); // Make sure the root can receive key events
                root.requestFocus(); // Give focus to the root
            });

            masterSequence.play();
        }

        private Label createStyledLabel(String text) {
            Label label = new Label(text);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 36)); // Modern, bold font
            label.setTextFill(Color.WHITE); // White text
            label.setWrapText(true); // Allow text to wrap if the window is too small
            label.setMaxWidth(800); // Max width before wrapping
            return label;
        }

    public static void main(String[] args) {
        launch(args);
    }

}
