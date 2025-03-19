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
import org.example.algorithmgrader.Util.Logija;

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
    @FXML
    private Button eelminePuuOlek;
    @FXML
    private Button lukustaPuu;
    @FXML
    private Label juhend;
    private Kahendotsimispuu puu;
    private Kahendotsimispuu visuaalnePuu;
    private Kahendotsimispuu eelnevaSeisugaPuu;
    private Tipp praeguneTipp;
    private List<Tipp> järjend = new ArrayList<>();
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<VisuaalneTipp> pesad = new ArrayList<>();
    private List<Tipp> aktiivsedTipud=new ArrayList<>();
    private final int JUURE_X = 400;
    private final int JUURE_Y = 50;
    private final int tipuRaadius = 20;
    private final int pesaX = 40;
    private final int pesaY = 60;
    private  final int pesaRaadius = 15;
    private int vigu = 0;
    private boolean lisatud;
    public void laePuu() {
        logiViga("\n");
        juhend.setText("Kasutusjuhend:\n" +
                "- Lukusta puu ja võta järgmine tipp: Kontrollib kas uus tipp on lisatud korrektselt,\n    ja võtab järjendist uue tipu lisamiseks.\n" +
                "- Eelmine puu olek: Laeb viimati lukustatud puu.\n" +
                "- 1 klikk tipul: Muudab tipu aktiivseks ja võimaldab lisada uue alluva.");
        kahendpuuAla.getChildren().clear();
        järjend.clear();
        visuaalsedTipud.clear();
        pesad.clear();
        vigu = 0;
        loeFailistVäärtused("sisendid/järjend1.txt");
        puu = new Kahendotsimispuu();
        visuaalnePuu = new Kahendotsimispuu();
        eelnevaSeisugaPuu = new Kahendotsimispuu();
        järgmineLisatavTipp();
        looPesad();
        lisatud=false;
    }
    private void looPesad() {
        kahendpuuAla.getChildren().removeAll(pesad);
        pesad.clear();

        if (visuaalsedTipud.isEmpty()){
            VisuaalneTipp juurPesa = new VisuaalneTipp(JUURE_X, JUURE_Y, 15, null);
            juurPesa.setFill(Color.LIGHTGRAY);

            pesaEventHandler(null, juurPesa, true);
            pesad.add(juurPesa);
            kahendpuuAla.getChildren().add(juurPesa);
        }/*else {
            for (VisuaalneTipp vt : visuaalsedTipud) {
                if (vt.tipp.vasak==null || vt.tipp.vasak==puu.viimatiLisatudTipp){
                    VisuaalneTipp vasakPesa = new VisuaalneTipp(vt.getCenterX() - pesaX, vt.getCenterY() + pesaY, pesaRaadius, null);

                    vasakPesa.setFill(Color.LIGHTGRAY);

                    pesaEventHandler(vasakPesa, true);

                    pesad.add(vasakPesa);
                    kahendpuuAla.getChildren().addAll(vasakPesa);

                    if(vt.tipp == puu.getVanem(puu.juurtipp, puu.viimatiLisatudTipp) && vt.tipp.väärtus > puu.viimatiLisatudTipp.väärtus) {
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

                    if(vt.tipp == puu.getVanem(puu.juurtipp, puu.viimatiLisatudTipp) && vt.tipp.väärtus <= puu.viimatiLisatudTipp.väärtus){
                        System.out.println("sobib parem");
                        System.out.println();
                        paremPesa.tipp=praeguneTipp;
                        pesad.remove(paremPesa);
                    }
                    vt.centerXProperty().addListener((obs, oldX, newX) -> paremPesa.setCenterX(((double) newX) +pesaX));
                    vt.centerYProperty().addListener((obs, oldY, newY) -> paremPesa.setCenterY(((double) newY) + pesaY));

                }
            }
            uuendaNooli();
        }*/
    }
    public void pesaEventHandler(Tipp tipp, VisuaalneTipp pesa, boolean vasak){
        pesa.setOnMouseClicked(e->{
            /*if(visuaalsedTipud.isEmpty() || pesa.tipp == praeguneTipp) {
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
            }*/

            Tipp lisatav = new Tipp(praeguneTipp.väärtus);
            VisuaalneTipp visuaalneTipp = new VisuaalneTipp(pesa.getCenterX(), pesa.getCenterY(), tipuRaadius, lisatav);
            if (puu.juurtipp == praeguneTipp) {
                visuaalnePuu.juurtipp = lisatav;
                lisaTippEkraanile(visuaalnePuu.juurtipp, visuaalneTipp, vasak);
            }
            else if (vasak){
                tipp.visuaalneTipp.setFill(Color.GRAY);
                tipp.vasak = lisatav;
                lisaTippEkraanile(tipp.vasak, visuaalneTipp, true);
            }else {
                tipp.visuaalneTipp.setFill(Color.GRAY);
                tipp.parem = lisatav;
                lisaTippEkraanile(tipp.parem, visuaalneTipp, false);
            }
            aktiivsedTipud.remove(tipp);
            lukustaPuu.setVisible(true);
            lisatud = true;
            järgmineTippLabel.setText("Lisatav tipp: Lisatud");
            kahendpuuAla.getChildren().removeAll(pesad);
            uuendaNooli();
        });
    }
    public void lisaTippEkraanile(Tipp tipp, VisuaalneTipp visuaalneTipp, boolean vasak){
        //VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        tipp.visuaalneTipp = visuaalneTipp;
        visuaalneTipp.setRadius(tipuRaadius);
        visuaalneTipp.setFill(Color.GRAY);

        kahendpuuAla.getChildren().add(liigutatavTipp(tipp, vasak));
        /*if (praeguneTipp!=puu.juurtipp){
            kahendpuuAla.getChildren().add(liigutatavTipp(tipp, vasak));
        }else {
            Text tekst = new Text(visuaalneTipp.tipp==null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

            tekst.setX(visuaalneTipp.getCenterX() - 4);
            tekst.setY(visuaalneTipp.getCenterY() + 4);
            kahendpuuAla.getChildren().add(new Group(visuaalneTipp, tekst));
        }*/

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
            lukustaPuu.setVisible(false);
            lisatud=false;

            praeguneTipp = järjend.get(0);
            puu.lisa(praeguneTipp);

            järgmineTippLabel.setText("Lisatav tipp: " + (järjend.isEmpty() ? "Kõik lisatud" : järjend.get(0).getVäärtus()));
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

    public Group liigutatavTipp(Tipp tipp, boolean vasak) {
        VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        Text tekst = new Text(visuaalneTipp.tipp==null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

        tekst.setX(visuaalneTipp.getCenterX() - 4);
        tekst.setY(visuaalneTipp.getCenterY() + 4);

        Group grupp = new Group(visuaalneTipp, tekst);
        grupp.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (tipp == visuaalnePuu.juurtipp) return;
            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40) return;
            //if (e.getY() < 35 || e.getY() > kahendpuuAla.getHeight() - 35) return;

            if (vasak && e.getX() > visuaalnePuu.getVanem(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX()-tipuRaadius) return;

            else if (!vasak && e.getX() < visuaalnePuu.getVanem(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX()+tipuRaadius) return;

            if (tipp.vasak!=null && tipp.vasak.visuaalneTipp!=null && e.getX() < tipp.vasak.visuaalneTipp.getCenterX()+tipuRaadius) return;
            else if (tipp.parem!=null && tipp.parem.visuaalneTipp!=null && e.getX() > tipp.parem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            visuaalneTipp.setCenterX(e.getX());
            //tipp.setCenterY(e.getY());
            tekst.setX(e.getX() - 4);
            //tekst.setY(e.getY() + 4);
            uuendaNooli();
        });
        grupp.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 1) {

                if (visuaalneTipp.getFill()==Color.GREEN && !lisatud){
                    aktiivsedTipud.remove(tipp);
                    kahendpuuAla.getChildren().removeAll(pesad);
                    pesad.clear();
                    visuaalneTipp.setFill(Color.GRAY);
                    uuendaNooli();
                } else if (visuaalneTipp.getFill()==Color.GRAY && !lisatud && aktiivsedTipud.isEmpty()) {
                    aktiivsedTipud.add(tipp);
                    visuaalneTipp.setFill(Color.GREEN);

                    if (tipp.vasak == null) {
                        VisuaalneTipp vasakPesa = new VisuaalneTipp(visuaalneTipp.getCenterX() - pesaX, visuaalneTipp.getCenterY() + pesaY, pesaRaadius, null);

                        vasakPesa.setFill(Color.LIGHTGRAY);

                        pesaEventHandler(tipp, vasakPesa, true);

                        pesad.add(vasakPesa);
                        kahendpuuAla.getChildren().add(vasakPesa);

                        visuaalneTipp.centerXProperty().addListener((obs, oldX, newX) -> {
                            vasakPesa.setCenterX(((double) newX) - pesaX);
                        });
                        visuaalneTipp.centerYProperty().addListener((obs, oldY, newY) -> vasakPesa.setCenterY(((double) newY) + pesaY));

                    }
                    if (tipp.parem == null) {
                        VisuaalneTipp paremPesa = new VisuaalneTipp(visuaalneTipp.getCenterX() + pesaX, visuaalneTipp.getCenterY() + pesaY, pesaRaadius, null);

                        paremPesa.setFill(Color.LIGHTGRAY);

                        pesaEventHandler(tipp, paremPesa, false);

                        pesad.add(paremPesa);
                        kahendpuuAla.getChildren().add(paremPesa);

                        visuaalneTipp.centerXProperty().addListener((obs, oldX, newX) -> {
                            paremPesa.setCenterX(((double) newX) + pesaX);

                        });
                        visuaalneTipp.centerYProperty().addListener((obs, oldY, newY) -> paremPesa.setCenterY(((double) newY) + pesaY));


                    }
                } else if (aktiivsedTipud.size()==1) {
                    kuvaTeade("","Korraga saab valida ühe tipu");
                }

                uuendaNooli();

            }
        });

        return grupp;
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
            if (vt.getFill()==Color.GREEN &&  (vt.tipp.parem == null || vt.tipp.parem.visuaalneTipp ==null) && !pesad.isEmpty()) {
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()+pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }
            if (vt.getFill()==Color.GREEN && (vt.tipp.vasak == null || vt.tipp.vasak.visuaalneTipp ==null) && !pesad.isEmpty()){
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()-pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }
        }
        kahendpuuAla.getChildren().addAll(nooled);
    }
    public void lukustaPuuOlek(){
        if(visuaalnePuu.kasOnKahendotsimispuu(visuaalnePuu.juurtipp, Integer.MIN_VALUE, Integer.MAX_VALUE, true)) {
            if(kasPuudOnSamad(puu.juurtipp, visuaalnePuu.juurtipp)){
                eelnevaSeisugaPuu = new Kahendotsimispuu();
                puudSamaks(eelnevaSeisugaPuu, puu.juurtipp);

                System.out.println("\n");
                puu.printPuuJaVisuaalnePuu(puu.juurtipp);
                System.out.println("\n");
                visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
                järjend.remove(0);
                ilusPuu();
                järgmineLisatavTipp();
            }
        }else {
            vigu++;
            System.out.println("\n");
            puu.printPuuJaVisuaalnePuu(puu.juurtipp);
            System.out.println("\n");
            visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
            kuvaTeade("","Ebakorrektne lisamine ja puu ei ole enam Kahendotsimispuu!");
            Logija.logiViga("Lisamine ebakorrektne, andmestruktuur ei ole enam kahendotsimispuu\n", "järjend_kahendotsimispuuks");
            visuaalnePuu = new Kahendotsimispuu();
            System.out.println("\n");
            eelnevaSeisugaPuu.printPuuJaVisuaalnePuu(eelnevaSeisugaPuu.juurtipp);
            puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
            lisatud=false;
            järgmineTippLabel.setText("Lisatav tipp: " + järjend.get(0).väärtus);
            ilusPuu();
        }
        uuendaNooli();
        System.out.println("\n");

    }
    public void laeEelnevPuu(){
        visuaalnePuu = new Kahendotsimispuu();
        puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
        lisatud=false;
        visuaalsedTipud.clear();
        ilusPuu();
        looPesad();
        uuendaNooli();
    }
    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();
        visuaalsedTipud.clear();
        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);

    }
    public void looVisuaalnePuu(Tipp tipp, int tase, int x, int y, boolean vasak){
        if (tipp== null)
            return;

        tipp.visuaalneTipp = new VisuaalneTipp(x, y, tipuRaadius, tipp);
        tipp.visuaalneTipp.setFill(Color.GRAY);

        kahendpuuAla.getChildren().add(liigutatavTipp(tipp, vasak));
        visuaalsedTipud.add(tipp.visuaalneTipp);
        tipp.visuaalneTipp.väärtus = tipp.väärtus;

        int xKoordMuutus=(int) (JUURE_X/Math.pow(2, tase));

        //liigutatavTipp(tipp.visuaalneTipp);

        looVisuaalnePuu(tipp.vasak, tase+1, x-xKoordMuutus, y+pesaY, true);
        looVisuaalnePuu(tipp.parem, tase+1, x+xKoordMuutus, y+pesaY, false);
    }
    private boolean kasPuudOnSamad(Tipp pJuur, Tipp vJuur){
        if (pJuur==null && vJuur==null)
            return true;
        if (pJuur==null || vJuur == null)
            return false;

        if (pJuur.väärtus==vJuur.väärtus) {
            return kasPuudOnSamad(pJuur.vasak, vJuur.vasak) && kasPuudOnSamad(pJuur.parem, vJuur.parem);
        }
        else
            return false;
    }
    private void puudSamaks(Kahendotsimispuu p, Tipp vTipp){
        if(vTipp==null)
            return;
        p.lisa(new Tipp(vTipp.väärtus));
        puudSamaks(p, vTipp.vasak);
        puudSamaks(p, vTipp.parem);

    }
    private void kuvaTeade(String pealkiri, String sisu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(kahendpuuAla.getScene().getWindow());
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

