package org.example.hackathon;
import java.util.Map;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas; // --- ADDED ---
import javafx.scene.canvas.GraphicsContext; // --- ADDED ---
import javafx.scene.control.Button; // --- ADDED ---
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.Map;
import java.util.Random; // --- ADDED ---

public class Launcher extends Application {

    //Sound value
    public static float audioValue;

    // ... (INTRO_TEXT array and constants remain unchanged) ...
    private static final String[] INTRO_TEXT = {
            "Five years ago, in 2030, humanity's dream of touching Mars became reality.",
            "Now, in 2035, you are a radio communicator, establishing contact with those on Mars. The existence of aliens has been suspected for years now.",
            "The long silence is shattered by a single, desperate transmission.",
            "\"...static... reactor failure... life support critical... can anyone hear me...?\"",
            "The signal dies. Their fate is now in your hands.",
            "Will you answer the call? Or be a coward?"
    };

    private static final Duration FADE_DURATION = Duration.seconds(2.5);
    private static final Duration DISPLAY_DURATION = Duration.seconds(2.0);
    private static final Duration PAUSE_BETWEEN_LINES = Duration.seconds(1.0);
    private static final Duration SHORT_PAUSE = Duration.seconds(1.5);
    private static final Duration TYPING_SPEED_PER_CHAR = Duration.millis(30);

    private Map<String, Dialogue> dialogues;
    private StackPane root = new StackPane();
    private SequentialTransition activeSequence = null;
    private Timeline activeTypingTimeline = null;
    private EventHandler<KeyEvent> dialogueKeyHandler = null;

    @Override
    public void start(Stage primaryStage) {

        root.setStyle("-fx-background-color: black;");
        root.setAlignment(Pos.CENTER);

        // --- Use 1280x720 for a more standard game resolution ---
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Mars 2035: The Distress Call");
        primaryStage.setScene(scene);
        primaryStage.show();

        playIntroAnimation(root, primaryStage);
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

        // --- Skip Intro Logic ---
        Label skipLabel = new Label("Press Enter to skip");
        skipLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        skipLabel.setTextFill(Color.GRAY);
        skipLabel.setPadding(new Insets(10));
        StackPane.setAlignment(skipLabel, Pos.BOTTOM_RIGHT);
        root.getChildren().add(skipLabel);

        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                masterSequence.stop();
                root.getChildren().clear();
                showTitleMenu(primaryStage);
            }
        });
        root.setFocusTraversable(true);
        root.requestFocus();

        // --- Part 4: OnFinished handler (MODIFIED) ---
        masterSequence.setOnFinished(event -> {
            root.getChildren().remove(skipLabel); // Clean up skip label
            showTitleMenu(primaryStage);
        });

        masterSequence.play();
    }

    // --- NEW METHOD: Replaces ArtistClass.start() ---
    private void showTitleMenu(Stage primaryStage) {

        // === 1. Build the Menu Scene (logic from ArtistClass) ===
        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ArtistClass.drawStars(gc, 2000); // Draw initial stars

        // Make stars redraw if window is resized
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());
        canvas.widthProperty().addListener((obs, o, n) -> ArtistClass.drawStars(gc, 2000));
        canvas.heightProperty().addListener((obs, o, n) -> ArtistClass.drawStars(gc, 2000));

        Label title = new Label("RED SIGNAL"); // Changed title
        title.setTextFill(Color.WHITE);
        // Use a more thematic font style
        title.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 100px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.8), 10, 0.5, 0, 0);");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';");
        // Add hover effect
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3; -fx-font-size: 32px; -fx-font-family: 'Arial';"));

        // --- This is where the GameInterface is now launched ---
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked, loading game...");
            launchGame(primaryStage); // Call helper to launch main game
        });

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

        try {
            // Get the resource URL from the classpath
            java.net.URL musicUrl = getClass().getResource("/music/spaceMusic.wav");
            if (musicUrl == null) {
                System.out.println("⚠️ Music file not found in resources!");
            } else {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // loop music
                clip.start();

                // Connect volume slider
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    float min = volumeControl.getMinimum();
                    float max = volumeControl.getMaximum();
                    float value = min + (max - min) * newVal.floatValue();
                    audioValue = value;
                    volumeControl.setValue(value);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HBox sliderBox = new HBox(volumeSlider);
        sliderBox.setAlignment(Pos.CENTER);
        sliderBox.setPrefWidth(200);

        VBox vbox = new VBox(20, title, startButton, volumeLabel, sliderBox);
        vbox.setAlignment(Pos.CENTER);

        StackPane menuRoot = new StackPane(canvas, vbox);
        menuRoot.setStyle("-fx-background-color: black;");
        Transitions.flashbang(primaryStage, menuRoot);
    }

    private void launchGame(Stage primaryStage) {
        // Load all dialogues (if not done yet)



        // Create main Game UI
        GameInterface mainUI = new GameInterface(primaryStage);

        loadDialogues();

        System.out.println(this.dialogues);

        // Switch to game UI
        Transitions.flashbang(primaryStage, mainUI.getRoot());

        // Start reading the JSON from the starting node
        playDialogueNodeInGame("start", mainUI);
    }




    private void playDialogueNodeInGame(String id, GameInterface ui) {
        Dialogue d = dialogues.get(id);
        if (d == null) {
            System.err.println("Dialogue ID not found: " + id);
            return;
        }

        // Sequentially display each line with a delay
        SequentialTransition seq = new SequentialTransition();

        for (String line : d.lines) {
            boolean isControl = line.startsWith("Control:");
            boolean onLeftSide = isControl;

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> ui.addChatMessage(line, onLeftSide));

            seq.getChildren().add(pause);
        }

        // After lines, handle choices or end
        seq.setOnFinished(event -> {
            if (d.choices == null || d.choices.isEmpty()) {
                ui.addChatMessage("[END OF TRANSMISSION]", true);
                return;
            }
            ui.showChoices(d.choices, choice -> {
                ui.clearChoices();
                playDialogueNodeInGame(choice.nextId, ui);
            });
        });

        seq.play();
    }


    private void loadDialogues() {
        try {
            java.net.URL dialogueUrl = getClass().getResource("/dialogues.json");
            System.out.println(dialogueUrl);
            if (dialogueUrl != null) {
                String jsonContent = new String(dialogueUrl.openStream().readAllBytes());
                dialogues = DialogueParser.parseDialogues(jsonContent);
            }
        } catch (Exception e) {
            System.err.println("Could not load dialogues: " + e.getMessage());
            dialogues = new java.util.HashMap<>();
        }
    }


    // Helper method for consistent styling
    private Label createStyledLabel(String text) {
        Label label = new Label("");
        Font retroFont = null;
        try {
            // NOTE: Make sure your font file is named this or fix the path!
            retroFont = Font.loadFont(getClass().getResourceAsStream("/fonts/digital-7 (mono).ttf"), 60);
        } catch (Exception e) {
            System.err.println("Could not load font! Using default. Error: " + e.getMessage());
        }
        if (retroFont != null) {
            label.setFont(retroFont);
        } else {
            label.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        }
        label.setTextFill(Color.color(0.0588235,0.58823529,0.0588235));
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