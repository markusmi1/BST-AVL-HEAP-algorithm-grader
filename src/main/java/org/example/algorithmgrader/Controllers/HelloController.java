package org.example.algorithmgrader.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class HelloController {

    public Tab jarjend_BST;
    public Tab eemaldamine_BST;

    @FXML
    public void initialize(){
        jarjend_BST.setOnSelectionChanged(e -> {

        });
        eemaldamine_BST.setOnSelectionChanged(e -> {
            if (eemaldamine_BST.isSelected()) {
                Dialog<Void> dialog = new Dialog<>();//TODO: meetodiks teha
                dialog.setContentText("");
                dialog.setTitle("");
                dialog.getDialogPane()
                        .getScene()
                        .getWindow()
                        .setOnCloseRequest(f -> {
                            dialog.hide();
                        });
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                dialog.showAndWait();
            };
        });
    }
}