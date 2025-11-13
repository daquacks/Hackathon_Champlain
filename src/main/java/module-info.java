module org.example.hackathon {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    requires javafx.graphics;
    requires javafx.base;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens org.example.hackathon to javafx.fxml, com.google.gson;
    exports org.example.hackathon;
}