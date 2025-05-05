package org.example.algorithmgrader.Controllers;

import static org.example.algorithmgrader.Util.Koordinaadid.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
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
    private List<String> vead= new ArrayList<>();
    private int vigu;
    private boolean lisatud;
    private String sisendFail = "sisendid/järjendKahendotsmispuuks.txt";
    private String logiFail = "järjendKahendotimispuuks_logi.txt";
    public void laePuu() {
        juhend.setText("Kasutusjuhend:\n" +
                "- Kontrolli lisamist: Kontrollib kas tipp on lisatud korrektselt, ja võtab järjendist uue tipu lisamiseks.\n" +
                "- Eelmine puu olek: Laeb viimati kontrollitud puu.\n" +
                "- Klikk tipul: Muudab tipu aktiivseks, võimaldab lisada uue alluva ja muuta tipu väärtust.");
        kahendpuuAla.getChildren().clear();
        järjend.clear();
        visuaalsedTipud.clear();
        pesad.clear();
        vead=new ArrayList<>();
        vigu = 0;
        loeFailistVäärtused(sisendFail);
        //logidesse töödeldav järjend
        vead.add("Sisend: " + järjend.toString());

        puu = new Kahendotsimispuu();
        visuaalnePuu = new Kahendotsimispuu();
        eelnevaSeisugaPuu = new Kahendotsimispuu();
        järgmineLisatavTipp();
        looPesad();
        lisatud=false;
        laeUusPuu.setVisible(false);
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
        }
    }
    public void pesaEventHandler(Tipp tipp, VisuaalneTipp pesa, boolean vasak){
        pesa.setOnMouseClicked(e->{
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
            eelminePuuOlek.setVisible(true);
            järgmineTippLabel.setText("Lisa tipp: Lisatud");
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

        visuaalsedTipud.add(visuaalneTipp);
    }

    private void loeFailistVäärtused(String failitee) {
        try {
            List<String> sisu = Files.readAllLines(Path.of(failitee));
            for (String rida : sisu) {
                rida = rida.replace("[", "").replace("]","");
                for (String väärtus : rida.split(","))
                    järjend.add(new Tipp(Integer.parseInt(väärtus.strip())));
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
            puu.lisa(praeguneTipp, false);

            järgmineTippLabel.setText("Lisa tipp: " + (järjend.isEmpty() ? "Kõik lisatud" : järjend.get(0).getVäärtus()));
            järgmisedTipudLabel.setText("Järjend: " + järjend.subList(1,järjend.size()).toString());

        } else {
            järgmineTippLabel.setText("Kõik elemendid lisatud!");
            järgmisedTipudLabel.setText("");
            kahendpuuAla.getChildren().removeAll(pesad);
            pesad.clear();
            uuendaNooli();

            //Logime vead
            Logija.logiViga(vead, logiFail);
            kuvaTeade("Läbimäng tehtud", "Vigu kokku: " + vigu);
            laeUusPuu.setVisible(true);
        }
    }
    private void looPesad(Tipp tipp, VisuaalneTipp visuaalneTipp){
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

            if (vasak && e.getX() > visuaalnePuu.getVanem(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX()-tipuRaadius) return;

            else if (!vasak && e.getX() < visuaalnePuu.getVanem(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX()+tipuRaadius) return;

            if (tipp.vasak!=null && tipp.vasak.visuaalneTipp!=null && e.getX() < tipp.vasak.visuaalneTipp.getCenterX()+tipuRaadius) return;

            else if (tipp.parem!=null && tipp.parem.visuaalneTipp!=null && e.getX() > tipp.parem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            visuaalneTipp.setCenterX(e.getX());

            tekst.setX(e.getX() - 4);

            uuendaNooli();
        });
        grupp.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 1) {
                StringBuilder inputText = new StringBuilder(tekst.getText());
                if(!lisatud && visuaalneTipp.getFill()==Color.GRAY){
                    if (!aktiivsedTipud.isEmpty()){
                        aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                        aktiivsedTipud.remove(aktiivsedTipud.get(0));
                        kahendpuuAla.getChildren().removeAll(pesad);
                        pesad.clear();
                    }

                    visuaalneTipp.setFill(Color.GREEN);
                    aktiivsedTipud.add(tipp);
                    looPesad(tipp, visuaalneTipp);
                    uuendaNooli();
                }
                else if (visuaalneTipp.getFill()==Color.GREEN){
                    aktiivsedTipud.remove(tipp);
                    if (!lisatud) {
                        kahendpuuAla.getChildren().removeAll(pesad);
                        pesad.clear();
                    }
                    visuaalneTipp.setFill(Color.GRAY);
                    uuendaNooli();
                } else if (visuaalneTipp.getFill()==Color.GRAY && aktiivsedTipud.isEmpty()) {
                    aktiivsedTipud.add(tipp);
                    visuaalneTipp.setFill(Color.GREEN);

                } else if (aktiivsedTipud.size()==1) {
                    aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                    aktiivsedTipud.remove(aktiivsedTipud.get(0));

                    visuaalneTipp.setFill(Color.GREEN);
                    aktiivsedTipud.add(tipp);
                }

                grupp.requestFocus();
                grupp.addEventHandler(KeyEvent.KEY_TYPED, keyEvent ->  {

                    if (visuaalneTipp.getFill()==Color.GREEN) {
                        //hetkelMuudetakseTippu = true;
                        String input = keyEvent.getCharacter();
                        kahendpuuAla.getChildren().removeAll(pesad);
                        pesad.clear();
                        uuendaNooli();
                        if (input.matches("\\d") && inputText.length() < 3) {
                            inputText.append(input);
                            tekst.setText(inputText.toString());
                            tipp.väärtus=Integer.parseInt(inputText.toString());
                        } else if (input.matches("\b") && !inputText.isEmpty()) {
                            inputText.deleteCharAt(inputText.length() - 1);
                            tekst.setText(inputText.toString());
                            if (inputText.isEmpty())
                                tipp.väärtus=0;
                            else
                                tipp.väärtus=Integer.parseInt(inputText.toString());
                        } else if (input.equals("\r") || input.equals("\n")) {
                            if (!inputText.isEmpty()) {
                                visuaalneTipp.väärtus = (Integer.parseInt(inputText.toString()));
                                tipp.väärtus = (Integer.parseInt(inputText.toString()));
                                visuaalneTipp.setFill(Color.GRAY);
                            }
                            // grupp.removeEventHandler(KeyEvent.KEY_TYPED, keyEvent);
                            //hetkelMuudetakseTippu = false;
                            aktiivsedTipud.remove(tipp);

                            keyEvent.consume();
                        }
                    }

                });
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
            if (!lisatud && vt.getFill()==Color.GREEN &&  (vt.tipp.parem == null || vt.tipp.parem.visuaalneTipp ==null) && !pesad.isEmpty()) {
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()+pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }
            if (!lisatud && vt.getFill()==Color.GREEN && (vt.tipp.vasak == null || vt.tipp.vasak.visuaalneTipp ==null) && !pesad.isEmpty()){
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
        System.out.println(visuaalnePuu.juurtipp);
        if(visuaalnePuu.kasOnKahendotsimispuu(visuaalnePuu.juurtipp, Integer.MIN_VALUE, Integer.MAX_VALUE, true)) {
            if(kasPuudOnSamad(puu.juurtipp, visuaalnePuu.juurtipp)){
                eelnevaSeisugaPuu = new Kahendotsimispuu();
                puudSamaks(eelnevaSeisugaPuu, puu.juurtipp);

                vead.add(järjend.get(0) + " lisati korrektselt");
                järjend.remove(0);
                ilusPuu();
                aktiivsedTipud.clear();
                järgmineLisatavTipp();
            }else {
                vigu++;
                aktiivsedTipud.clear();
                puu = new Kahendotsimispuu();
                puudSamaks(puu, visuaalnePuu.juurtipp);

                kuvaTeade("","Ebakorrektne lisamine, kuid on säilitatud kahendotsimispuu");

                vead.add("VIGA: " + järjend.get(0) + " lisati ebakorrektselt, kuid on säilitatud kahendotsimispuu");
                järjend.remove(0);
                ilusPuu();
                järgmineLisatavTipp();
            }
        }else {
            vigu++;
            vead.add("VIGA: " + järjend.get(0) + " lisamisel kaotati kahendotsimispuu struktuur");
            kuvaTeade("","Ebakorrektne lisamine ja puu ei ole enam Kahendotsimispuu!");

            visuaalnePuu = new Kahendotsimispuu();
            puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
            aktiivsedTipud.clear();
            lisatud=false;
            järgmineTippLabel.setText("Lisa tipp: " + järjend.get(0).väärtus);
            ilusPuu();
        }
        eelminePuuOlek.setVisible(false);
        lukustaPuu.setVisible(false);
        uuendaNooli();
    }
    public void laeEelnevPuu(){
        visuaalnePuu = new Kahendotsimispuu();
        puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
        lisatud=false;
        visuaalsedTipud.clear();
        ilusPuu();
        looPesad();
        uuendaNooli();
        eelminePuuOlek.setVisible(false);
        lukustaPuu.setVisible(false);
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
        p.lisa(new Tipp(vTipp.väärtus), false);
        puudSamaks(p, vTipp.vasak);
        puudSamaks(p, vTipp.parem);

    }
    private void kuvaTeade(String pealkiri, String sisu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(kahendpuuAla.getScene().getWindow());
        alert.setTitle("Teavitus");
        alert.setHeaderText(pealkiri);
        alert.setContentText(sisu);
        alert.showAndWait();
    }
}

