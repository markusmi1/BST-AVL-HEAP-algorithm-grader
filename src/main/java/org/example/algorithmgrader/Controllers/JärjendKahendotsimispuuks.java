package org.example.algorithmgrader.Controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.algorithmgrader.Kahendpuu.Arrow;
import org.example.algorithmgrader.Kahendpuu.Kahendotsimispuu;
import org.example.algorithmgrader.Kahendpuu.Tipp;
import org.example.algorithmgrader.Kahendpuu.VisuaalneTipp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import javafx.scene.control.Label;

import java.nio.file.Files;
import java.util.List;

public class JärjendKahendotsimispuuks {
    @FXML
    private Pane kahendpuuAla; // Puu joonistamiseks

    @FXML
    private Label järgmineTippLabel; // Kuvab järgmise tipu
    @FXML
    private Label järgmisedTipudLabel;
    @FXML
    private Button laeUusPuu;
    private Kahendotsimispuu puu;
    private Tipp praeguneTipp;
    private List<Tipp> järjend = new ArrayList<>();
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<VisuaalneTipp> pesad = new ArrayList<>();
    private final int JUURE_X = 400;
    private final int JUURE_Y = 50;
    private final int tipuRaadius = 25;
    private final int pesaX = 40;
    private final int pesaY = 60;
    private  final int pesaRaadius = 15;
    private int vigu = 0;
    public void laePuu() {
        logiViga("\n");
        kahendpuuAla.getChildren().clear();
        järjend.clear();
        visuaalsedTipud.clear();
        pesad.clear();
        vigu = 0;
        loeFailistVäärtused("sisendid/järjend1.txt");
        puu = new Kahendotsimispuu();
        järgmineLisatavTipp();
        looPesad(null);
    }
    private void looPesad(VisuaalneTipp pesa) {
        kahendpuuAla.getChildren().removeAll(pesad);
        pesad.clear();

        if (visuaalsedTipud.isEmpty()){
            VisuaalneTipp juurPesa = new VisuaalneTipp(JUURE_X, JUURE_Y, 15, null);
            juurPesa.setFill(Color.LIGHTGRAY);

            pesaEventHandler(juurPesa, true);
            pesad.add(juurPesa);
            kahendpuuAla.getChildren().addAll(juurPesa);
        }else {
            for (VisuaalneTipp vt : visuaalsedTipud) {
                if (vt.tipp.vasak==null || vt.tipp.vasak==puu.viimatiLisatudTipp){
                    VisuaalneTipp vasakPesa = new VisuaalneTipp(vt.getCenterX() - pesaX, vt.getCenterY() + pesaY, pesaRaadius, null);

                    vasakPesa.setFill(Color.LIGHTGRAY);

                    pesaEventHandler(vasakPesa, true);

                    pesad.add(vasakPesa);
                    kahendpuuAla.getChildren().addAll(vasakPesa);

                    if(vt.tipp == puu.getVanem(puu.juurtipp, puu.viimatiLisatudTipp) && vt.tipp.väärtus >= puu.viimatiLisatudTipp.väärtus) {
                        System.out.println("sobib vasak");
                        vasakPesa.tipp = praeguneTipp;
                        pesad.remove(vasakPesa);
                    }
                    vt.centerXProperty().addListener((obs, oldX, newX) -> vasakPesa.setCenterX(((double) newX) -pesaX));
                    vt.centerYProperty().addListener((obs, oldY, newY) -> vasakPesa.setCenterY(((double) newY) + pesaY));

                }
                if ((vt.tipp.parem) == null || vt.tipp.parem==puu.viimatiLisatudTipp){
                    VisuaalneTipp paremPesa = new VisuaalneTipp(vt.getCenterX() + pesaX, vt.getCenterY() + pesaY, pesaRaadius, null);

                    paremPesa.setFill(Color.LIGHTGRAY);

                    pesaEventHandler(paremPesa, false);

                    pesad.add(paremPesa);
                    kahendpuuAla.getChildren().addAll(paremPesa);

                    if(vt.tipp == puu.getVanem(puu.juurtipp, puu.viimatiLisatudTipp) && vt.tipp.väärtus < puu.viimatiLisatudTipp.väärtus){
                        System.out.println("sobib parem");
                        paremPesa.tipp=praeguneTipp;
                        pesad.remove(paremPesa);
                    }
                    vt.centerXProperty().addListener((obs, oldX, newX) -> paremPesa.setCenterX(((double) newX) +pesaX));
                    vt.centerYProperty().addListener((obs, oldY, newY) -> paremPesa.setCenterY(((double) newY) + pesaY));

                }
            }
            uuendaNooli();
        }
    }
    public void pesaEventHandler(VisuaalneTipp pesa, boolean vasak){
        pesa.setOnMouseClicked(e->{
            if(visuaalsedTipud.isEmpty() || pesa.tipp == praeguneTipp) {
                VisuaalneTipp visuaalneTipp = new VisuaalneTipp(pesa.getCenterX(), pesa.getCenterY(), tipuRaadius, praeguneTipp);
                lisaTipp(visuaalneTipp, pesa, vasak);
                järjend.remove(0);
                järgmineLisatavTipp();
                if (!järjend.isEmpty())
                    looPesad(visuaalneTipp);
                else
                    kahendpuuAla.getChildren().removeAll(pesad);

                uuendaNooli();
            }else{
                kuvaTeade("Viga", "Vale koht");
                logiViga("Vale koht\n");
                vigu++;
            }
        });
    }
    public void lisaTipp(VisuaalneTipp visuaalneTipp, VisuaalneTipp pesa, boolean vasak){
        praeguneTipp.visuaalneTipp = visuaalneTipp;
        visuaalneTipp.setRadius(tipuRaadius);
        visuaalneTipp.setFill(Color.GREEN);
        kahendpuuAla.getChildren().remove(pesa);
        if (praeguneTipp!=puu.juurtipp){
            kahendpuuAla.getChildren().add(liigutatavTipp(visuaalneTipp, vasak));
        }else {
            Text tekst = new Text(visuaalneTipp.tipp==null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

            tekst.setX(visuaalneTipp.getCenterX() - 4);
            tekst.setY(visuaalneTipp.getCenterY() + 4);
            kahendpuuAla.getChildren().add(new Group(visuaalneTipp, tekst));
        }

        visuaalsedTipud.add(visuaalneTipp);
    }

    private void loeFailistVäärtused(String failitee) {
        try {
            List<String> sisu = Files.readAllLines(Path.of(failitee));
            for (String rida : sisu) {
                rida = rida.replace("[", "").replace("]","");
                for (String väärtus : rida.split(",\s++"))
                    järjend.add(new Tipp(Integer.parseInt(väärtus)));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    private void järgmineLisatavTipp() {
        if (!järjend.isEmpty()) {
            praeguneTipp = järjend.get(0);
            puu.lisa(praeguneTipp);

            järgmineTippLabel.setText("Praegune tipp: " + (järjend.isEmpty() ? "Kõik lisatud" : järjend.get(0).getVäärtus()));
            //järjend.remove(0);
            järgmisedTipudLabel.setText(järjend.subList(1,järjend.size()).toString());
        } else {
            järgmineTippLabel.setText("Kõik tipud lisatud!");
            järgmisedTipudLabel.setText("");
            kahendpuuAla.getChildren().removeAll(pesad);
            pesad.clear();
            logiViga("Vigu: " + vigu + "\n");
            uuendaNooli();
            kuvaTeade("Läbimäng tehtud", "Vigu kokku: " +vigu);
        }
    }

    public Group liigutatavTipp(VisuaalneTipp tipp, boolean vasak) {
        Text tekst = new Text(tipp.tipp==null ? "" : String.valueOf(tipp.tipp.getVäärtus()));

        tekst.setX(tipp.getCenterX() - 4);
        tekst.setY(tipp.getCenterY() + 4);

        tipp.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40) return;
            //if (e.getY() < 35 || e.getY() > kahendpuuAla.getHeight() - 35) return;

            if (vasak && e.getX() > puu.getVanem(puu.juurtipp, puu.tipud.get(puu.tipud.indexOf(tipp.tipp))).visuaalneTipp.getCenterX()-tipuRaadius) return;

            else if (!vasak && e.getX() < puu.getVanem(puu.juurtipp, puu.tipud.get(puu.tipud.indexOf(tipp.tipp))).visuaalneTipp.getCenterX()+tipuRaadius) return;

            if (tipp.tipp.vasak!=null && tipp.tipp.vasak.visuaalneTipp!=null && e.getX() < tipp.tipp.vasak.visuaalneTipp.getCenterX()+tipuRaadius) return;
            else if (tipp.tipp.parem!=null && tipp.tipp.parem.visuaalneTipp!=null && e.getX() > tipp.tipp.parem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            tipp.setCenterX(e.getX());
            //tipp.setCenterY(e.getY());
            tekst.setX(e.getX() - 4);
            //tekst.setY(e.getY() + 4);
            uuendaNooli();
        });

        return new Group(tipp, tekst);
    }
    private void uuendaNooli(){
        kahendpuuAla.getChildren().removeIf(e -> e instanceof Arrow);
        List<Arrow> nooled = new ArrayList<>();
        for (VisuaalneTipp vt : visuaalsedTipud) {
            if (vt.tipp.parem != null && vt.tipp.parem.visuaalneTipp!=null) {
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.tipp.parem.visuaalneTipp.getCenterX(), vt.tipp.parem.visuaalneTipp.getCenterY()
                );
                nooled.add(nool);
            }
            if (vt.tipp.vasak != null && vt.tipp.vasak.visuaalneTipp!=null){
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.tipp.vasak.visuaalneTipp.getCenterX(), vt.tipp.vasak.visuaalneTipp.getCenterY()
                );
                nooled.add(nool);
            }
            if ((vt.tipp.parem == null || vt.tipp.parem.visuaalneTipp ==null) && !pesad.isEmpty()) {
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()+pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }
            if ((vt.tipp.vasak == null || vt.tipp.vasak.visuaalneTipp ==null) && !pesad.isEmpty()){
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()-pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }
        }
        kahendpuuAla.getChildren().addAll(nooled);
    }

    private void kuvaTeade(String pealkiri, String sisu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Teavitus");
        alert.setHeaderText(pealkiri);
        alert.setContentText(sisu);
        alert.showAndWait();
    }
    private void logiViga(String viga){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("järjend_kahendotsimispuuks_logi.txt", true))) {
            bw.append(viga);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

