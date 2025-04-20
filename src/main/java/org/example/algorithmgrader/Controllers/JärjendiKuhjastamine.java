package org.example.algorithmgrader.Controllers;

import static org.example.algorithmgrader.Util.Koordinaadid.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.algorithmgrader.Kahendpuu.*;
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
    Button lukustaPuu;
    @FXML
    Button eelminePuuOlek;
    @FXML
    private Label järgmineTippLabel; // Kuvab järgmise tipu
    @FXML
    private Label järgmisedTipudLabel;
    @FXML
    private Button laeUusPuu;
    @FXML
    private Label juhend;
    private Kuhi kuhi;
    private Kuhi visuaalneKuhi;
    private List<Integer> eelnevaSeisugaKuhi;
    private Tipp praeguneTipp;
    private Tipp kuhjaJuurtipp;
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<Tipp> aktiivsedTipud = new ArrayList<>();
    private List<Integer> järjend = new ArrayList<>();
    private List<VisuaalneTipp> pesad = new ArrayList<>();
    private List<String> vead = new ArrayList<>();
    private int vigu = 0;
    private boolean elemendidLisatud;
    private final String sisendFail = "sisendid/järjendiKuhjastamine.txt";
    private final String logiFail = "kuhjastamine_logi.txt";
    public void laePuu() {
        juhend.setText("Kasutusjuhend:\n" +
                "Lisa elemendid järjendist kompaktsesse kahendpuusse,\n"+
                "seejärel kuhjasta kompaktne kahendpuu\n"+
                "- Kontrolli kuhjastatud puud: Kontrollib kuhjastamise lõpptulemust\n" +
                "- Vaheta tipud: Saab vahetada 2 tippu vastavalt kuhjastamise algoritmile\n");

        kahendpuuAla.getChildren().clear();
        järjend.clear();
        visuaalsedTipud.clear();
        pesad.clear();
        vead=new ArrayList<>();
        vigu = 0;
        loeFailistVäärtused(sisendFail);
        vead.add("Sisend: " + järjend + "\n");

        kuhi = new Kuhi();
        visuaalneKuhi = new Kuhi();
        järgmineLisatavTipp();
        looJuurPesa();
        elemendidLisatud =false;
        laeUusPuu.setVisible(false);
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
                for (String väärtus : rida.split(","))
                    järjend.add(Integer.parseInt(väärtus.strip()));
            }
        } catch (IOException e) {
            System.err.println("Vales formaadis fail!");
            System.out.println(e.getMessage());
        }
    }

    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();
        visuaalsedTipud.clear();
        kuhjaJuurtipp = visuaalneKuhi.looTipp(0);

        looVisuaalneKuhi(kuhjaJuurtipp, 1, JUURE_X, JUURE_Y, true);
    }
    public void looVisuaalneKuhi(Tipp tipp, int tase, int x, int y, boolean vasak){
        if (tipp == null)
            return;

        tipp.visuaalneTipp = new VisuaalneTipp(x, y, tipuRaadius, tipp);
        tipp.visuaalneTipp.setFill(Color.GRAY);

        kahendpuuAla.getChildren().add(liigutatavTipp(tipp, vasak));
        visuaalsedTipud.add(tipp.visuaalneTipp);
        tipp.visuaalneTipp.väärtus = tipp.väärtus;

        int xKoordMuutus=(int) (JUURE_X/Math.pow(2, tase));

        //liigutatavTipp(tipp.visuaalneTipp);

        looVisuaalneKuhi(tipp.vasak, tase+1, x-xKoordMuutus, y+pesaY, true);
        looVisuaalneKuhi(tipp.parem, tase+1, x+xKoordMuutus, y+pesaY, false);
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
            if (visuaalsedTipud.isEmpty()) {
                visuaalneKuhi.lisaKirje(praeguneTipp.väärtus);
                lisaTippEkraanile(lisatavTipp, visuaalneTipp, vasak);
                ilusPuu();
            }
            else if (vasak){
                visuaalneKuhi.kuhi.add(praeguneTipp.väärtus);

                if (visuaalneKuhi.leiaÜlemuseIndeks(visuaalneKuhi.kuhi.size()-1)!=tipp.indeks){
                    vigu++;
                    vead.add("VIGA: Tippu " + praeguneTipp.väärtus + " lisades oleks kaotatud kompaktne kahendpuu");
                    kuvaTeade("Viga","Vale tipu lisamine, puu ei ole kompaktne");
                    visuaalneKuhi.kuhi.remove(visuaalneKuhi.kuhi.size()-1);
                    return;
                }
                tipp.visuaalneTipp.setFill(Color.GRAY);
                tipp.vasak = lisatavTipp;

                lisaTippEkraanile(tipp.vasak, visuaalneTipp, true);
                ilusPuu();
            }else {
                visuaalneKuhi.kuhi.add(praeguneTipp.väärtus);
                if (visuaalneKuhi.leiaÜlemuseIndeks(visuaalneKuhi.kuhi.size()-1)!=tipp.indeks || tipp.vasak==null){
                    kuvaTeade("Viga","Vale tipu lisamine, puu ei ole kompaktne");
                    visuaalneKuhi.kuhi.remove(visuaalneKuhi.kuhi.size()-1);
                    return;
                }
                tipp.visuaalneTipp.setFill(Color.GRAY);
                tipp.parem = lisatavTipp;
                lisaTippEkraanile(tipp.parem, visuaalneTipp, false);
                ilusPuu();
            }

            aktiivsedTipud.remove(tipp);
            //lukustaPuu.setVisible(true);
            //lisatud = true;
            järgmineTippLabel.setText("Kuhjasta puu!");
            kahendpuuAla.getChildren().removeAll(pesad);
            pesad.clear();
            //järgmineTipp.setVisible(true);
            järjend.remove(0);
            järgmineLisatavTipp();
            uuendaNooli();
        });
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
            lukustaPuu.setVisible(false);
            elemendidLisatud =false;
            eelnevaSeisugaKuhi = new ArrayList<>(kuhi.kuhi);

            praeguneTipp = new Tipp(järjend.get(0));
            kuhi.lisaKirje(järjend.get(0));

            järgmineTippLabel.setText("Lisa kompaktsesse kahendpuusse tipp: " + (järjend.isEmpty() ? "Järjend kuhjastatud" : järjend.get(0)));
            järgmisedTipudLabel.setText("Järjend: " + järjend.subList(1,järjend.size()).toString());

        } else {
            järgmineTippLabel.setText("Kuhjasta puu!");
            järgmisedTipudLabel.setText("");
            kahendpuuAla.getChildren().removeAll(pesad);
            pesad.clear();
            elemendidLisatud=true;
            lukustaPuu.setVisible(true);
            vead.set(0, vead.get(0) + "Kompaktne kahendpuu järjend: " + kuhi.kuhi);

            uuendaNooli();
        }
    }
    public Group liigutatavTipp(Tipp tipp, boolean vasak) {
        VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        Text tekst = new Text(visuaalneTipp.tipp == null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

        tekst.setX(visuaalneTipp.getCenterX() - 4);
        tekst.setY(visuaalneTipp.getCenterY() + 4);

        Group grupp = new Group(visuaalneTipp, tekst);
        grupp.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (tipp == kuhjaJuurtipp) return;
            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40)
                return;
            //if (e.getY() < 35 || e.getY() > kahendpuuAla.getHeight() - 35) return;

            if (vasak && e.getX() > getVanemKahendpuu(kuhjaJuurtipp, tipp).visuaalneTipp.getCenterX() - tipuRaadius)
                return;

            else if (!vasak && e.getX() < getVanemKahendpuu(kuhjaJuurtipp, tipp).visuaalneTipp.getCenterX() + tipuRaadius)
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
                    vahetaTipud.setVisible(false);
                    uuendaNooli();
                    return;
                }
                if(!elemendidLisatud && visuaalneTipp.getFill()==Color.GRAY){
                    if (!aktiivsedTipud.isEmpty()){
                        aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                        aktiivsedTipud.remove(aktiivsedTipud.get(0));
                        kahendpuuAla.getChildren().removeAll(pesad);
                        pesad.clear();
                    }

                    visuaalneTipp.setFill(Color.GREEN);
                    aktiivsedTipud.add(tipp);
                    looPesad(tipp, visuaalneTipp);
                    return;
                }

                if (aktiivsedTipud.size()<2){
                    aktiivsedTipud.add(tipp);
                    visuaalneTipp.setFill(Color.GREEN);
                    if (aktiivsedTipud.size()==2){
                        vahetaTipud.setVisible(true);
                    }
                }else if (aktiivsedTipud.size()==2){
                    aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                    aktiivsedTipud.remove(aktiivsedTipud.get(0));

                    visuaalneTipp.setFill(Color.GREEN);
                    aktiivsedTipud.add(tipp);
                }

            }

            e.consume();
            uuendaNooli();

        });
        vahetaTipud.setOnAction(e1 ->{
            vahetaKuhjaTipud();
            vahetaTipud.setVisible(false);
            aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
            aktiivsedTipud.get(1).visuaalneTipp.setFill(Color.GRAY);
            aktiivsedTipud.clear();
            uuendaNooli();
        });


        return grupp;
    }
    public void vahetaKuhjaTipud(){
        Tipp tipp1 = aktiivsedTipud.get(0);
        Tipp tipp2 = aktiivsedTipud.get(1);

        if ((visuaalneKuhi.leiaÜlemuseIndeks(tipp1.indeks)!=tipp2.indeks && visuaalneKuhi.leiaÜlemuseIndeks(tipp2.indeks)!=tipp1.indeks)){
            kuvaTeade("","Tippu saab vahetada ainult oma otsese ülemusega");
            return;
        }
        if (tipp2.indeks<tipp1.indeks) {

            if (!kasVahetusSobib(tipp2,tipp1)){
                return;
            }
        }else {
            if (!kasVahetusSobib(tipp1, tipp2)){
                return;
            }
        }

        int ajutine = tipp1.väärtus;
        tipp1.väärtus=tipp2.väärtus;
        tipp2.väärtus=ajutine;

        visuaalneKuhi.vaheta(tipp1.indeks, tipp2.indeks);

        ajutine = tipp1.indeks;
        tipp1.indeks = tipp2.indeks;
        tipp2.indeks = ajutine;

        ilusPuu();
    }
    private boolean kasVahetusSobib(Tipp tipp, Tipp tipp2){
        if (tipp.väärtus>=tipp2.väärtus) {
            vigu++;
            vead.add("ALGORITMILINE VIGA: Ülemtipp väärtusega " + tipp.väärtus + " üritati vahetada alamtipuga " + tipp2.väärtus);
            kuvaTeade("Viga", "Vahetus ei vasta kuhjastamise algoritmile");
            return false;
        }
        if (tipp.vasak!=null) {
            if (tipp.vasak != tipp2 && tipp.vasak.väärtus > tipp2.väärtus){
                vigu++;
                vead.add("ALGORITMILINE VIGA: Ülemtipp väärtusega " + tipp.väärtus + " üritati vahetada parema alluvaga väärtusega "
                        + tipp2.väärtus + ", kui vasaku väärtus on " + tipp.vasak.väärtus);
                kuvaTeade("Viga", "Vahetus ei vasta kuhjastamise algoritmile.");
                return false;
            }
            if (!kasAlamkuhiOnKuhi(tipp.vasak)) {
                vigu++;
                vead.add("ALGORITMILINE VIGA: Vahetatavate tippude alamkuhjad ei vasta kuhjatingimustele");
                kuvaTeade("Viga", "Vahetatavate tippude alamkuhjad ei vasta kuhjatingimustele");
                return false;
            }
        }
        if (tipp.parem!=null) {
            if (tipp.parem != tipp2 && tipp.parem.väärtus > tipp2.väärtus){
                vigu++;
                vead.add("ALGORITMILINE VIGA: Ülemtipp väärtusega " + tipp.väärtus + " üritati vahetada vasaku alluvaga väärtusega "
                        + tipp2.väärtus + ", kui parema väärtus on " + tipp.parem.väärtus);
                kuvaTeade("Viga", "Vahetus ei vasta kuhjastamise algoritmile..");
                return false;
            }
            if (!kasAlamkuhiOnKuhi(tipp.parem)) {
                vigu++;
                vead.add("ALGORITMILINE VIGA: Vahetatavate tippude alamkuhjad ei vasta kuhjatingimustele");
                kuvaTeade("Viga", "Vahetatavate tippude alamkuhi ei vasta kuhjatingimustele");
                return false;
            }
        }
        return true;
    }

    public boolean kasAlamkuhiOnKuhi(Tipp juur){
        if (juur==null){
            return true;
        }
        boolean v=true;
        boolean p=true;

        if (juur.vasak!=null){
            if (juur.väärtus>=juur.vasak.väärtus){
                v = kasAlamkuhiOnKuhi(juur.vasak);
            }else {
                return  false;
            }
        }
        if (juur.parem!=null){
            if (juur.väärtus>=juur.parem.väärtus){
                p = kasAlamkuhiOnKuhi(juur.parem);
            }else {
                return  false;
            }
        }

        return v && p;
    }
    public void lukustaPuuOlek(){
        if(visuaalneKuhi.kasOnKuhi()) {
            järgmineTippLabel.setText("Järjend kuhjastatud!");
            vead.add("Vigu: " + vigu);
            Logija.logiViga(vead, logiFail);
            laeUusPuu.setVisible(true);
            vahetaTipud.setVisible(false);
            lukustaPuu.setVisible(false);
            kuvaTeade("Läbimäng tehtud", "Vigu kokku: " + vigu);
        }else {
            vigu++;
            vead.add("VIGA: Kuhi peaks olema - " + kuhi.kuhi + ", aga on - " + visuaalneKuhi.kuhi);
            kuvaTeade("","Kahendkuhi ei vasta kuhja tingimustele!");
        }
    }
    public void laeEelnevKuhi(){
        visuaalneKuhi.kuhi= new ArrayList<>(eelnevaSeisugaKuhi);
        elemendidLisatud =false;
        visuaalsedTipud.clear();
        järgmineTippLabel.setText("Lisa kompaktsesse kahendpuusse tipp: " + järjend.get(0));
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
            if (aktiivsedTipud.size()==1 && vt.getFill()== Color.GREEN &&  (vt.tipp.parem == null || vt.tipp.parem.visuaalneTipp ==null) && !pesad.isEmpty()) {
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()+pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }
            if (aktiivsedTipud.size()==1 && vt.getFill()==Color.GREEN && (vt.tipp.vasak == null || vt.tipp.vasak.visuaalneTipp ==null) && !pesad.isEmpty()){
                Arrow nool = new Arrow(
                        vt.getCenterX(), vt.getCenterY(),
                        vt.getCenterX()-pesaX, vt.getCenterY()+pesaY
                );
                nooled.add(nool);
            }

        }
        kahendpuuAla.getChildren().addAll(nooled);
    }
    public Tipp getVanemKahendpuu(Tipp tipp, Tipp otsitav) {
        if (tipp == null || otsitav == kuhjaJuurtipp) {
            return null;
        }
        if (tipp.vasak == otsitav || tipp.parem == otsitav) {
            return tipp;
        }

        Tipp vasak = getVanemKahendpuu(tipp.vasak, otsitav);
        if (vasak != null) return vasak;

        return getVanemKahendpuu(tipp.parem, otsitav);
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
