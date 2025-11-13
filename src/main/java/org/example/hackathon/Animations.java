package org.example.hackathon;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Animations {
    public static void flashbang(Stage primaryStage, Node menuRoot) {
        // === 2. Build the Flash-Bang Transition ===

        // Create the white flash pane, matching the scene size
        Rectangle flashPane = new Rectangle(primaryStage.getWidth(), primaryStage.getHeight(), Color.WHITE);
        flashPane.setOpacity(1.0); // Start fully white

        // Put the menu *under* the white flash pane
        StackPane transitionRoot = new StackPane(menuRoot, flashPane);

        // Create the new scene with the transition layout
        Scene menuScene = new Scene(transitionRoot, 1280, 720);

        // Set the new scene immediately (it's just a white screen)
        primaryStage.setScene(menuScene);

        // === 3. Animate the Fade-In ===
        // Create a fade-out for the *white pane*, revealing the menu underneath
        FadeTransition fadeFromWhite = new FadeTransition(Duration.seconds(1.5), flashPane);
        fadeFromWhite.setFromValue(1.0);
        fadeFromWhite.setToValue(0.0);

        // After fading, remove the flash pane so it doesn't block clicks
        fadeFromWhite.setOnFinished(e -> {
            flashPane.setVisible(false);
            flashPane.setManaged(false); // Completely remove from layout
        });

        fadeFromWhite.play();
    }
}
