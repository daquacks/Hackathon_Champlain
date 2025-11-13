package org.example.hackathon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node; // Import Node
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

        VBox distressCallBox = new VBox(5); // 5px spacing
        distressCallBox.setAlignment(Pos.CENTER);
        distressCallBox.getChildren().addAll(label2, label3);
        root.getChildren().add(distressCallBox);

        ParallelTransition fadeOutBoth = new ParallelTransition(
                createFadeOut(label2),
                createFadeOut(label3)
        );

        SequentialTransition distressSequence = new SequentialTransition(
                createFadeIn(label2),
                new PauseTransition(SHORT_PAUSE),
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

        // --- MODIFICATION HERE ---

        // 1. Create the fade-out transitions
        FadeTransition fadeOut4 = createFadeOut(label4);
        FadeTransition fadeOut5 = createFadeOut(label5);

        // 2. Create the shake animation for the VBox
        Animation shakeAnimation = createShake(choiceBox);

        // 3. Combine fades and shake into one parallel transition
        ParallelTransition fadeOutAndShake = new ParallelTransition(
                fadeOut4,
                fadeOut5,
                shakeAnimation
        );

        // 4. Build the sequence using the new parallel transition
        SequentialTransition choiceSequence = new SequentialTransition(
                createFadeIn(label4),
                new PauseTransition(SHORT_PAUSE), // Shorter pause before line 5 appears
                createFadeIn(label5),
                new PauseTransition(DISPLAY_DURATION),
                fadeOutAndShake // Use the new combined animation
        );

        // --- END MODIFICATION ---

        masterSequence.getChildren().add(choiceSequence);
        // Add a final pause before the "Press Any Key" screen
        masterSequence.getChildren().add(new PauseTransition(PAUSE_BETWEEN_LINES));


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

    // --- NEW HELPER METHOD ---
    /**
     * Creates a shake animation for a given Node.
     * @param node The node to shake.
     * @return An Animation (Timeline) that shakes the node.
     */
    private Animation createShake(Node node) {
        Timeline timeline = new Timeline();
        // The duration of one back-and-forth "shake" cycle
        double cycleDuration = 75; // 75 milliseconds

        timeline.getKeyFrames().addAll(
                // At 0ms, text is centered
                new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
                // At 25% of cycle, move left
                new KeyFrame(Duration.millis(cycleDuration * 0.25), new KeyValue(node.translateXProperty(), -8)),
                // At 75% of cycle, move right
                new KeyFrame(Duration.millis(cycleDuration * 0.75), new KeyValue(node.translateXProperty(), 8)),
                // At 100% of cycle, return to center
                new KeyFrame(Duration.millis(cycleDuration), new KeyValue(node.translateXProperty(), 0))
        );

        // Calculate how many times to shake during the fade duration
        int shakeCycles = (int) (FADE_DURATION.toMillis() / cycleDuration);
        timeline.setCycleCount(shakeCycles);

        return timeline;
    }


    public static void main(String[] args) {
        launch(args);
    }
}