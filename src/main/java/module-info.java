module org.example.algorithmgrader {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.algorithmgrader;
    opens org.example.algorithmgrader.Controllers;
    opens org.example.algorithmgrader.Kahendpuu;
    opens org.example.algorithmgrader.Util;
}