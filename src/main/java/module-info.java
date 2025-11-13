module org.example.hackathon {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.hackathon to javafx.fxml;
    exports org.example.hackathon;
}