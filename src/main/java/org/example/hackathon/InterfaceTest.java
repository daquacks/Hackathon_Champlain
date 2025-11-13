package org.example.hackathon;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class InterfaceTest extends Application {

        @Override
        public void start(Stage primaryStage) {
            GameInterface ui = new GameInterface(primaryStage);

            // Add initial chat messages
            ui.addChatMessage("Incoming transmission detected...", true);
            ui.addChatMessage("Commander Hale: This is Hale... is anyone reading me?", true);

            // Simulate player response
            ui.addChatMessage("Control Center: We read you, Commander. What's your situation?", false);

            // Add a discovered landmark
            ui.addMapLandmark("Crimson Ridge", 150, 80);

            // Update resources gradually to simulate passage of time
            Timeline resourceTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), e -> ui.updateResource("Oxygen", 0.85)),
                    new KeyFrame(Duration.seconds(4), e -> ui.updateResource("Morale", 0.75)),
                    new KeyFrame(Duration.seconds(6), e -> ui.updateResource("Connection", 0.65)),
                    new KeyFrame(Duration.seconds(8), e -> ui.updateResource("Food Supply", 0.5))
            );
            resourceTimeline.play();

            // Add more chat messages to fill the scrollable window
            Timeline chatTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(3), e -> ui.addChatMessage("Commander Hale: Oxygen levels stable, but I’ve lost visual contact with base.", true)),
                    new KeyFrame(Duration.seconds(6), e -> ui.addChatMessage("Control Center: Stay calm. Can you locate any landmarks?", false)),
                    new KeyFrame(Duration.seconds(9), e -> ui.addChatMessage("Commander Hale: There’s a ridge to the north... adding it to the map.", true)),
                    new KeyFrame(Duration.seconds(12), e -> ui.addMapLandmark("Delta Crater", 250, 140))
            );
            chatTimeline.play();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
