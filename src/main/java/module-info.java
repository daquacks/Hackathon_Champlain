module org.example.hackathon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;



    opens org.example.hackathon to javafx.fxml;
    exports org.example.hackathon;
}