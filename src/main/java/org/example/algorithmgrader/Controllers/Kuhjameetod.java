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
import javafx.scene.text.TextFlow;
import org.example.algorithmgrader.Kahendpuu.*;
import org.example.algorithmgrader.Util.Logija;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Kuhjameetod {
    @FXML
    private Pane kahendpuuAla;
    @FXML
    Button vahetaTipud;
    @FXML
    Button märgiTöödelduks;
    @FXML
    Button lukustaPuu;
    @FXML
    TextFlow massiivFlow;
    @FXML
    Button laeUusPuu;
    @FXML
    private Label juhend;
    @FXML
    private Button laeEelnevPuu;

    private Kuhi kuhi;
    private Kuhi visuaalneKuhi;
    private List<Integer> eelnevaSeisugaKuhi;
    private Tipp kuhjaJuurtipp;
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<Tipp> aktiivsedTipud = new ArrayList<>();
    private List<Integer> järjend = new ArrayList<>();
    private List<Integer> töödeldud = new ArrayList<>();
    private List<Integer> massiiv = new ArrayList<>();
    private List<String> vead = new ArrayList<>();
    private int vigu = 0;
    boolean juurTöödeldud;

    private final String sisendFail = "sisendid/kuhjameetod.txt";
    private final String logiFail = "kuhjameetod_logi.txt";


    public void laeKuhi() {
        juhend.setText("Kasutusjuhend:\n" +
                "- Kontrolli sammu: Kontrollib kuhjameetodi sammu, Samm on 1 elemendi töötlemine ja kuhja struktuuri taastamine.\n" +
                "- Eelmine puu olek: Laeb viimati kontrollitud kuhja.\n" +
                "- Märgi element töödelduks: Lisab elemendi massiivi lõppu ja eemaldab kuhja struktuurist.\n" +
                "- Vaheta tipud: Vahetab omavahel kaks aktiivset tippu.\n");
        kahendpuuAla.getChildren().clear();
        järjend.clear();
        visuaalsedTipud.clear();
        aktiivsedTipud.clear();
        töödeldud.clear();
        vead=new ArrayList<>();
        juurTöödeldud=false;

        vigu = 0;

        kuhi = new Kuhi();
        visuaalneKuhi = new Kuhi();
        loeFailistVäärtused(sisendFail);
        vead.add("Sisend kuhi:\n"+ kuhi.kuhi.toString());
        töötleJärgmine();
        ilusPuu();
        uuendaNooli();
        laeUusPuu.setVisible(false);

    }
    private void loeFailistVäärtused(String failitee) {
        try {
            List<String> sisu = Files.readAllLines(Path.of(failitee));

            for (String rida : sisu) {
                rida = rida.replace("[", "").replace("]","");
                for (String väärtus : rida.split(",")) {
                    //kuhjastame järjendi
                    kuhi.lisaKirje(Integer.parseInt(väärtus.strip()));
                    visuaalneKuhi.lisaKirje(Integer.parseInt(väärtus.strip()));
                }
            }
        } catch (IOException e) {
            System.err.println("Vales formaadis fail!");
            System.out.println(e.getMessage());
        }
    }
    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();
        visuaalsedTipud.clear();
        if(!visuaalneKuhi.kuhi.isEmpty()) {
            kuhjaJuurtipp = visuaalneKuhi.looTipp(0);
            looVisuaalneKuhi(kuhjaJuurtipp, 1, JUURE_X, JUURE_Y, true);
        }
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

        looVisuaalneKuhi(tipp.vasak, tase+1, x-xKoordMuutus, y+pesaY, true);
        looVisuaalneKuhi(tipp.parem, tase+1, x+xKoordMuutus, y+pesaY, false);
    }
    public void töötleJärgmine(){
        lukustaPuu.setVisible(false);
        märgiTöödelduks.setVisible(false);
        if (!visuaalneKuhi.kuhi.isEmpty()) {
            juurTöödeldud=false;
            eelnevaSeisugaKuhi = new ArrayList<>(kuhi.kuhi);
            if(!kuhi.kuhi.isEmpty())
                kuhi.eemaldaJuur();

            massiiv=new ArrayList<>(visuaalneKuhi.kuhi);

            visuaalneMassiiv();

        } else {
            Text tekst = new Text("Kuhi sorteeritud: ");
            Text töödeldudText = new Text( töödeldud.toString());
            töödeldudText.setFill(Color.GREEN);
            massiivFlow.getChildren().setAll(tekst, töödeldudText);

            Logija.logiViga(vead, logiFail);

            kuvaTeade("Läbimäng tehtud", "Vigu kokku: " +vigu);
            laeUusPuu.setVisible(true);
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
                    vahetaTipud.setVisible(false);
                    if (aktiivsedTipud.size()==1 && !juurTöödeldud){
                        märgiTöödelduks.setVisible(true);
                    }else {
                        märgiTöödelduks.setVisible(false);
                    }
                    uuendaNooli();
                    return;
                }
                if (aktiivsedTipud.size()<2){
                    aktiivsedTipud.add(tipp);
                    visuaalneTipp.setFill(Color.GREEN);
                    if (aktiivsedTipud.size()==1) {
                        vahetaTipud.setVisible(false);
                        if (!juurTöödeldud) {
                            märgiTöödelduks.setVisible(true);
                        }
                    }
                    if (aktiivsedTipud.size()==2){
                        vahetaTipud.setVisible(true);
                        märgiTöödelduks.setVisible(false);
                    }
                } else if (aktiivsedTipud.size() == 2) {
                    aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                    aktiivsedTipud.remove(aktiivsedTipud.get(0));

                    visuaalneTipp.setFill(Color.GREEN);
                    aktiivsedTipud.add(tipp);
                }
            }
            vahetaTipud.setOnAction(e1 ->{
                vahetaKuhjaTipud();
                vahetaTipud.setVisible(false);
                aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                aktiivsedTipud.get(1).visuaalneTipp.setFill(Color.GRAY);
                aktiivsedTipud.clear();
                uuendaNooli();
            });
            märgiTöödelduks.setOnAction(e2 -> {
                if (aktiivsedTipud.get(0).väärtus != visuaalneKuhi.kuhi.get(visuaalneKuhi.kuhi.size()-1)){
                    vigu++;
                    vead.add("ALGORITMILINE VIGA: Üritati töödelda elementi mis ei olnud kuhja viimane element");
                    kuvaTeade("Viga", "Töödelduks saab märkida ainult kuhja viimast elementi");
                    return;
                }
                visuaalneKuhi.kuhi.remove(visuaalneKuhi.kuhi.size()-1);
                töödeldud.add(0, aktiivsedTipud.get(0).väärtus);
                massiiv=new ArrayList<>(visuaalneKuhi.kuhi);

                visuaalneMassiiv();

                aktiivsedTipud.clear();
                juurTöödeldud=true;
                märgiTöödelduks.setVisible(false);
                lukustaPuu.setVisible(true);
                laeEelnevPuu.setVisible(true);
                ilusPuu();
                uuendaNooli();
            });

            e.consume();
            uuendaNooli();

        });

        return grupp;
    }
    public void laeEelnevPuu(){
        visuaalneKuhi = new Kuhi();
        for (int i = 0; i < eelnevaSeisugaKuhi.size(); i++) {
            visuaalneKuhi.kuhi.add(i, eelnevaSeisugaKuhi.get(i));
        }
        visuaalsedTipud.clear();
        aktiivsedTipud.clear();
        töödeldud.remove(0);
        massiiv=new ArrayList<>(visuaalneKuhi.kuhi);
        visuaalneMassiiv();
        ilusPuu();
        uuendaNooli();
        juurTöödeldud=false;

        laeEelnevPuu.setVisible(false);
        lukustaPuu.setVisible(false);
        vahetaTipud.setVisible(false);
    }
    public void vahetaKuhjaTipud(){
        Tipp tipp1 = aktiivsedTipud.get(0);
        Tipp tipp2 = aktiivsedTipud.get(1);

        int ajutine = tipp1.väärtus;
        tipp1.väärtus=tipp2.väärtus;
        tipp2.väärtus=ajutine;

        visuaalneKuhi.vaheta(tipp1.indeks, tipp2.indeks);

        ajutine = tipp1.indeks;
        tipp1.indeks = tipp2.indeks;
        tipp2.indeks = ajutine;

        massiiv=new ArrayList<>(visuaalneKuhi.kuhi);

        visuaalneMassiiv();

        ilusPuu();
    }
    public void lukustaKuhi(){
        if (!juurTöödeldud){
            kuvaTeade("","Töötle vastavalt kuhjameetodile üks tipp");
            return;
        }
        if(visuaalneKuhi.kuhi.equals(kuhi.kuhi)) {
            vead.add(töödeldud.get(0)+" töödeldi korrektselt");

            töötleJärgmine();
        }else {
            vigu++;
            vead.add("VIGA: " + "Õige kuhi - " + kuhi.kuhi + ", Loodud kuhi - " + (visuaalneKuhi.kuhi));
            kuvaTeade("","Ebakorrektne kuhjameetodi samm");

            visuaalneKuhi.kuhi= new ArrayList<>(eelnevaSeisugaKuhi);
            visuaalsedTipud.clear();
            töödeldud.remove(0);
            aktiivsedTipud.clear();
            ilusPuu();
            juurTöödeldud=false;

            uuendaNooli();
            massiiv=new ArrayList<>(visuaalneKuhi.kuhi);

            visuaalneMassiiv();

        }
        lukustaPuu.setVisible(false);
        laeEelnevPuu.setVisible(false);
        vahetaTipud.setVisible(false);
        System.out.println("\n");

    }
    private void visuaalneMassiiv(){
        if (töödeldud.isEmpty()){
            Text massiivText = new Text("Massiiv: " + massiiv);
            massiivFlow.getChildren().setAll(massiivText);
        } else if (massiiv.isEmpty()) {
            Text tekst = new Text("Massiiv: ");
            Text töödeldudText = new Text(töödeldud.toString());
            töödeldudText.setFill(Color.GREEN);
            massiivFlow.getChildren().setAll(tekst, töödeldudText);
        } else {
            Text massiivText = new Text("Massiiv: " + massiiv.toString().substring(0, massiiv.toString().length() - 1) + ", ");
            Text töödeldudText = new Text(töödeldud.toString().substring(1));
            töödeldudText.setFill(Color.GREEN);
            massiivFlow.getChildren().setAll(massiivText, töödeldudText);
        }
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
