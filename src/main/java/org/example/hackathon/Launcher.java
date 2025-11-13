package org.example.hackathon;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Launcher extends Application {

    private static final String[] INTRO_TEXT = {
            "Five years ago, in 2030, humanity's dream of touching Mars became reality.",
            "Now, in 2035, you are the sole lifeline... the only voice connecting the Ares-7 colony back to Earth.",
            "The long silence is shattered by a single, desperate transmission.",
            "\"...static... reactor failure... life support critical... can anyone hear me...?\"",
            "The signal dies. Their fate is now in your hands.",
            "Will you answer the call?"
    };

    private static final Duration FADE_DURATION = Duration.seconds(2.5);
    private static final Duration DISPLAY_DURATION = Duration.seconds(2.0);
    private static final Duration PAUSE_BETWEEN_LINES = Duration.seconds(1.0);
    private static final Duration SHORT_PAUSE = Duration.seconds(1.5);
    private static final Duration TYPING_SPEED_PER_CHAR = Duration.millis(30);

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Red Signal: A Message from Mars");
        primaryStage.setScene(scene);
        primaryStage.show();

        playIntroAnimation(root, primaryStage);
    }

    private void playIntroAnimation(StackPane root, Stage primaryStage) {
        SequentialTransition masterSequence = new SequentialTransition();

        // Animate the intro text lines
        for (int i = 0; i < INTRO_TEXT.length; i++) {
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

        // Add skip functionality
        Label skipLabel = new Label("Press Enter to skip");
        skipLabel.setFont(Font.font("Arial", 24));
        skipLabel.setTextFill(Color.GRAY);
        skipLabel.setPadding(new Insets(10));
        StackPane.setAlignment(skipLabel, Pos.BOTTOM_RIGHT);
        root.getChildren().add(skipLabel);

        root.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                masterSequence.stop();
                root.getChildren().clear();
                showTitleMenu(primaryStage);
            }
        });
        root.requestFocus();

        masterSequence.setOnFinished(event -> {
            root.getChildren().remove(skipLabel);
            showTitleMenu(primaryStage);
        });

        masterSequence.play();
    }

    private void showTitleMenu(Stage primaryStage) {
        StackPane menuRoot = new StackPane();
        menuRoot.setStyle("-fx-background-color: black;");

        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ArtistClass.drawStars(gc, 2000);

        canvas.widthProperty().bind(menuRoot.widthProperty());
        canvas.heightProperty().bind(menuRoot.heightProperty());
        canvas.widthProperty().addListener((obs, o, n) -> ArtistClass.drawStars(gc, 2000));
        canvas.heightProperty().addListener((obs, o, n) -> ArtistClass.drawStars(gc, 2000));

        Label title = new Label("RED SIGNAL");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 100px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.8), 10, 0.5, 0, 0);");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));
        startButton.setOnAction(e -> launchGame(primaryStage));

        Label volumeLabel = new Label("Volume");
        volumeLabel.setTextFill(Color.WHITE);
        volumeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 20px;");

        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setPrefWidth(250);

        try {
            java.net.URL musicUrl = getClass().getResource("/music/spaceMusic.wav");
            if (musicUrl != null) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();

                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    float min = volumeControl.getMinimum();
                    float max = volumeControl.getMaximum();
                    float value = min + (max - min) * newVal.floatValue();
                    volumeControl.setValue(value);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox vbox = new VBox(20, title, startButton, volumeLabel, volumeSlider);
        vbox.setAlignment(Pos.CENTER);

        menuRoot.getChildren().addAll(canvas, vbox);
        Transitions.flashbang(primaryStage, menuRoot);
    }

    private void launchGame(Stage primaryStage) {
        GameInterface mainUI = new GameInterface(primaryStage);
        Transitions.flashbang(primaryStage, mainUI.getRoot());

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
                new KeyFrame(Duration.seconds(12), ev -> mainUI.addMapLandmark("Delta Crater", 250, 140))
        );
        chatTimeline.play();
    }

    private Label createStyledLabel(String text) {
        Label label = new Label("");
        Font retroFont = null;
        try {
            retroFont = Font.loadFont(getClass().getResourceAsStream("/fonts/digital-7 (mono).ttf"), 60);
        } catch (Exception e) {
            System.err.println("Could not load font! Using default. Error: " + e.getMessage());
        }
        label.setFont(retroFont != null ? retroFont : Font.font("Arial", FontWeight.BOLD, 36));
        label.setTextFill(Color.color(0.0588235, 0.58823529, 0.0588235));
        label.setWrapText(true);
        label.setMaxWidth(800);
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    private Animation createTypingAnimation(Label label, String fullText) {
        final ObjectProperty<Integer> letterCount = new SimpleObjectProperty<>(0);
        label.textProperty().bind(Bindings.createStringBinding(() ->
                fullText.substring(0, Math.min(letterCount.get(), fullText.length())), letterCount));
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(TYPING_SPEED_PER_CHAR.multiply(fullText.length()),
                new KeyValue(letterCount, fullText.length(), Interpolator.LINEAR)));
        return timeline;
    }

    private FadeTransition createFadeOut(Node node) {
        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        return fadeOut;
    }

    public static void main(String[] args) {
        launch(args);
    }
}