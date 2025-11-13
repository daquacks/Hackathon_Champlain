package org.example.hackathon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;

import javafx.scene.layout.VBox;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //pane 1
        VBox vbox1 = new VBox(5);
        Label lbl1 = new Label("Pane 1");
        TextField txf1 = new TextField();
        txf1.setPrefColumnCount(4);
        Button btn1 = new Button("Select");
        btn1.setOnAction(e -> System.out.println("Pane 1 button pressed"));
        vbox1.getChildren().addAll(lbl1, txf1, btn1);

        //pane 2
        //pane 1
        VBox vbox2 = new VBox(5);
        Label lbl2 = new Label("Pane 2");
        TextField txf2 = new TextField();
        txf1.setPrefColumnCount(4);
        Button btn2 = new Button("Select");
        btn2.setOnAction(e -> System.out.println("Pane 2 button pressed"));
        vbox2.getChildren().addAll(lbl2, txf2, btn2);

        //splitPane
        SplitPane splitPane = new SplitPane(); //horizontal by default
        splitPane.getItems().addAll(vbox1, vbox2);

        Scene scene = new Scene(splitPane,620, 250);
        stage.setTitle("Split Pane Demo");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
