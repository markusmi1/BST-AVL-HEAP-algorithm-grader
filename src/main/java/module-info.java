module org.example.algorithmgrader {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;


    exports org.example.algorithmgrader;
    exports org.example.algorithmgrader.Controllers;

    opens org.example.algorithmgrader to javafx.fxml;
    opens org.example.algorithmgrader.Controllers to javafx.fxml;
}