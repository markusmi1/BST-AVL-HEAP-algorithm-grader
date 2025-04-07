package org.example.algorithmgrader.Controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.algorithmgrader.Kahendpuu.Arrow;
import org.example.algorithmgrader.Kahendpuu.Kahendotsimispuu;
import org.example.algorithmgrader.Kahendpuu.Tipp;
import org.example.algorithmgrader.Kahendpuu.VisuaalneTipp;
import org.example.algorithmgrader.Util.Logija;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JärjendiKuhjastamine {
    @FXML
    private Pane kahendpuuAla;
    @FXML
    Button vahetaTipud;
    @FXML
    Button laeJärjend;
    @FXML
    Button järgmineTipp;
    @FXML
    Button vaheta;
    @FXML
    private Label järgmineTippLabel; // Kuvab järgmise tipu
    @FXML
    private Label järgmisedTipudLabel;
    private Kahendotsimispuu puu;
    private Kahendotsimispuu visuaalnePuu;
    private Kahendotsimispuu eelnevaSeisugaPuu;
    private Tipp praeguneTipp;
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<Tipp> aktiivsedTipud = new ArrayList<>();
    private List<Integer> järjend = new ArrayList<>();
    private List<VisuaalneTipp> pesad = new ArrayList<>();
    private final int JUURE_X = 400;
    private final int JUURE_Y = 50;
    private final int tipuRaadius = 20;
    private final int pesaX = 40;
    private final int pesaY = 60;
    private  final int pesaRaadius = 15;
    private int vigu = 0;
    private boolean lisatud;
    public void laePuu() {
        Logija.logiViga("\n", "järjendi_kuhjastamine");
        kahendpuuAla.getChildren().clear();
        järjend.clear();
        visuaalsedTipud.clear();
        pesad.clear();
        vigu = 0;
        loeFailistVäärtused("sisendid/järjendiKuhjastamine.txt");
        puu = new Kahendotsimispuu();
        visuaalnePuu = new Kahendotsimispuu();
        eelnevaSeisugaPuu = new Kahendotsimispuu();
        järgmineLisatavTipp();
        looJuurPesa();
        lisatud=false;
    }
    private void looJuurPesa() {
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

    private void loeFailistVäärtused(String failitee) {
        try {
            List<String> sisu = Files.readAllLines(Path.of(failitee));
            for (String rida : sisu) {
                rida = rida.replace("[", "").replace("]","");
                for (String väärtus : rida.split(",\s++"))
                    järjend.add(Integer.parseInt(väärtus));
            }
        } catch (IOException e) {
            System.err.println("Vales formaadis fail!");
            System.out.println(e.getMessage());
        }
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
    public void lisaTippEkraanile(Tipp tipp, VisuaalneTipp visuaalneTipp, boolean vasak){
        //VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        tipp.visuaalneTipp = visuaalneTipp;
        visuaalneTipp.setRadius(tipuRaadius);
        visuaalneTipp.setFill(Color.GRAY);

        kahendpuuAla.getChildren().add(liigutatavTipp(tipp, vasak));

        visuaalsedTipud.add(visuaalneTipp);
    }
    public void pesaEventHandler(Tipp tipp, VisuaalneTipp pesa, boolean vasak){
        pesa.setOnMouseClicked(e->{
            Tipp lisatavTipp = new Tipp(praeguneTipp.väärtus);
            VisuaalneTipp visuaalneTipp = new VisuaalneTipp(pesa.getCenterX(), pesa.getCenterY(), tipuRaadius, lisatavTipp);
            if (puu.juurtipp == praeguneTipp) {
                visuaalnePuu.juurtipp = lisatavTipp;
                lisaTippEkraanile(visuaalnePuu.juurtipp, visuaalneTipp, vasak);
            }
            else if (vasak){
                tipp.visuaalneTipp.setFill(Color.GRAY);
                tipp.vasak = lisatavTipp;
                lisaTippEkraanile(tipp.vasak, visuaalneTipp, true);
            }else {
                tipp.visuaalneTipp.setFill(Color.GRAY);
                tipp.parem = lisatavTipp;
                lisaTippEkraanile(tipp.parem, visuaalneTipp, false);
            }

            aktiivsedTipud.remove(tipp);
            //lukustaPuu.setVisible(true);
            lisatud = true;
            järgmineTippLabel.setText("Lisatav tipp: Lisatud");
            kahendpuuAla.getChildren().removeAll(pesad);
            pesad.clear();
            järgmineTipp.setVisible(true);
            uuendaNooli();
        });
    }
    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();

        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
    }
    public void looPesad(Tipp tipp, VisuaalneTipp visuaalneTipp){
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
        uuendaNooli();
    }
    private void järgmineLisatavTipp() {
        if (!järjend.isEmpty()) {
            järgmineTipp.setVisible(false);
            lisatud=false;

            praeguneTipp = new Tipp(järjend.get(0));
            puu.lisa(praeguneTipp, false);

            järgmineTippLabel.setText("Lisatav tipp: " + (järjend.isEmpty() ? "Järjend kuhjastatud" : järjend.get(0)));
            järgmisedTipudLabel.setText(järjend.subList(1,järjend.size()).toString());

        } else {
            järgmineTippLabel.setText("Järjend kuhjastatud!");
            järgmisedTipudLabel.setText("");
            kahendpuuAla.getChildren().removeAll(pesad);
            pesad.clear();
            Logija.logiViga("Vigu: " + vigu + "\n", "järjendi_kuhjastamine");
            uuendaNooli();
            kuvaTeade("Läbimäng tehtud", "Vigu kokku: " +vigu);
        }
    }
    public Group liigutatavTipp(Tipp tipp, boolean vasak) {
        VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        Text tekst = new Text(visuaalneTipp.tipp == null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

        tekst.setX(visuaalneTipp.getCenterX() - 4);
        tekst.setY(visuaalneTipp.getCenterY() + 4);

        Group grupp = new Group(visuaalneTipp, tekst);
        grupp.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (tipp == visuaalnePuu.juurtipp) return;
            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40)
                return;
            //if (e.getY() < 35 || e.getY() > kahendpuuAla.getHeight() - 35) return;

            if (vasak && e.getX() > visuaalnePuu.getVanem(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX() - tipuRaadius)
                return;

            else if (!vasak && e.getX() < visuaalnePuu.getVanem(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX() + tipuRaadius)
                return;

            if (tipp.vasak != null && tipp.vasak.visuaalneTipp != null && e.getX() < tipp.vasak.visuaalneTipp.getCenterX() + tipuRaadius)
                return;
            else if (tipp.parem != null && tipp.parem.visuaalneTipp != null && e.getX() > tipp.parem.visuaalneTipp.getCenterX() - tipuRaadius)
                return;

            visuaalneTipp.setCenterX(e.getX());
            //tipp.setCenterY(e.getY());
            tekst.setX(e.getX() - 4);
            //tekst.setY(e.getY() + 4);
            uuendaNooli();
        });
        grupp.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount()==1){
                if(visuaalneTipp.getFill() == Color.GREEN){
                    aktiivsedTipud.remove(tipp);
                    visuaalneTipp.setFill(Color.GRAY);
                    kahendpuuAla.getChildren().removeAll(pesad);
                    pesad.clear();
                    uuendaNooli();
                    return;
                }
                if (aktiivsedTipud.size()<2){
                    aktiivsedTipud.add(tipp);
                    visuaalneTipp.setFill(Color.GREEN);

                }else {
                    kuvaTeade("Info","Korraga saab olla valitud 2 tippu!");
                }
                if (!lisatud && visuaalneTipp.getFill()==Color.GREEN){
                    looPesad(tipp, visuaalneTipp);
                }
            }

            e.consume();
            uuendaNooli();

        });

        return grupp;
    }
    public void lukustaPuuOlek(){
        if (!lisatud){
            kuvaTeade("","Enne tipu lisamist ei saa puud lukku panna");
            return;
        }
        if(visuaalnePuu.kasOnKahendotsimispuu(visuaalnePuu.juurtipp, Integer.MIN_VALUE, Integer.MAX_VALUE, true)) {
            if(kasPuudOnSamad(puu.juurtipp, visuaalnePuu.juurtipp)){
                System.out.println("\n");
                puu.printPuuJaVisuaalnePuu(puu.juurtipp);
                System.out.println("\n");
                visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
                järjend.remove(0);
                järgmineLisatavTipp();

            }else {
                System.out.println("\n");
                puu.printPuuJaVisuaalnePuu(puu.juurtipp);
                System.out.println("\n");
                visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
                kuvaTeade("","Ebakorrektne lisamine, kuid on säilitatud kahendpuu");
                Logija.logiViga("Ebakorrektne elemendi lisamine, kuid on säilitatud kahendpuu\n", "järjendi_kuhjastamine");

                //TODO logi viga
                järjend.remove(0);
                järgmineLisatavTipp();

            }
        }else {
            System.out.println("\n");
            puu.printPuuJaVisuaalnePuu(puu.juurtipp);
            System.out.println("\n");
            visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
            kuvaTeade("","Ebakorrektne lisamine ja puu ei ole enam kahendpuu!");
            Logija.logiViga("Ebakorrektne lisamine, puu ei ole enam kahendotsimispuu\n", "järjendi_kuhjastamine");
            visuaalnePuu = new Kahendotsimispuu();
            puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
            visuaalsedTipud.clear();
            ilusPuu();
            lisatud=false;
            järgmineTippLabel.setText("Lisatav tipp: " + järjend.get(0));
            uuendaNooli();

        }
        System.out.println("\n");

    }
    public void laeEelnevPuu(){
        visuaalnePuu = new Kahendotsimispuu();
        puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
        lisatud=false;
        visuaalsedTipud.clear();
        ilusPuu();
        uuendaNooli();
    }
    private void uuendaNooli(){
        kahendpuuAla.getChildren().removeIf(e -> e instanceof Arrow);
        List<Arrow> nooled = new ArrayList<>();
        for (VisuaalneTipp vt : visuaalsedTipud) {
            if (vt.tipp.parem != null && vt.tipp.parem.visuaalneTipp.tipp!=null ) {
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.tipp.parem.visuaalneTipp.getCenterX(), vt.tipp.parem.visuaalneTipp.getCenterY()
                );
                nooled.add(nool);
            }
            if (vt.tipp.vasak != null && vt.tipp.vasak.visuaalneTipp!=null && visuaalsedTipud.contains(vt.tipp.vasak.visuaalneTipp)){
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.tipp.vasak.visuaalneTipp.getCenterX(), vt.tipp.vasak.visuaalneTipp.getCenterY()
                );
                nooled.add(nool);
            }
            if (vt.getFill()== Color.GREEN &&  (vt.tipp.parem == null || vt.tipp.parem.visuaalneTipp ==null) && !pesad.isEmpty()) {
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
        //isegi kui on avl teeb nagu nii sama puu
        p.lisa(new Tipp(vTipp.väärtus), false);
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

}
