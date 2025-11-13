module org.example.hackathon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;


    opens org.example.hackathon to javafx.fxml;
    exports org.example.hackathon;
}