module org.example.hackathon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;


    requires javafx.graphics;
    requires javafx.base;

    opens org.example.hackathon to javafx.fxml, com.google.gson;
    exports org.example.hackathon;
}