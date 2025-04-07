package org.example.algorithmgrader.Kahendpuu;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class VisuaalneTipp extends Circle {
    public Tipp tipp;
    public int väärtus;
    public String tekst;

    public VisuaalneTipp(double centerX, double centerY, double raadius, Tipp tipp) {
        super(centerX, centerY, raadius);
        this.tipp = tipp;
    }
}
