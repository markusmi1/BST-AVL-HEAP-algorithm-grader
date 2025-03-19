package org.example.algorithmgrader.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logija {
    public static void logiViga(String viga, String algoritm){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(algoritm + "_logi.txt", true))) {
            bw.append(viga + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
