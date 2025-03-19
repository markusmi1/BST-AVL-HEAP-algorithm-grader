package org.example.algorithmgrader.Kahendpuu;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * eeskuju - https://gist.github.com/kn0412/2086581e98a32c8dfa1f69772f14bca4
 * eeskuju - https://github.com/ErikPresnov/GraphGrader/blob/main/src/main/java/com/example/graphgrader/Graaf/Arrow.java
 */
public class Arrow extends Path{
    private static final double arrowHeadSize = 5.0;
    public Arrow(double startX, double startY, double endX, double endY){
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.BLACK);

        //ArrowHead
        double angle = Math.atan2((endY - startY), (endX - startX));
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        startX += 28 * cos;
        startY += 28 * sin;

        double angle2 = angle - Math.PI;
        double sin2 = Math.sin(angle2);
        double cos2 = Math.cos(angle2);

        endX += 28 * cos2;
        endY += 28 * sin2;

        getElements().add(new MoveTo(startX , startY));
        getElements().add(new LineTo(endX, endY));

        sin = Math.sin(angle - Math.PI / 2.0);
        cos = Math.cos(angle - Math.PI / 2.0);

        double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
        double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

        getElements().add(new LineTo(x1, y1));
        getElements().add(new LineTo(x2, y2));
        getElements().add(new LineTo(endX, endY));
    }

}