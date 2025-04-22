package org.example.algorithmgrader.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Logija {
    public static void logiViga(List<String> vead, String logiFail){
        File logiKaust = new File("logi");
        if (!logiKaust.exists())
            logiKaust.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("logi/" + logiFail, false))) {
            bw.append(vead.get(0)).append("\n\n");
            for (String viga : vead.subList(1, vead.size())) {
                bw.append(viga).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
