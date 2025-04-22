package org.example.algorithmgrader.Controllers;

import static org.example.algorithmgrader.Util.Koordinaadid.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
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

public class ElemendiEemaldamineBST {
    @FXML
    private Pane kahendpuuAla;
    @FXML
    private Button laeUusPuu;
    @FXML
    private Button lukustaPuu;
    @FXML
    private Button kustutaTipp;
    @FXML
    private Button lisaParemAlluv;
    @FXML
    private Label eemaldatav;
    @FXML
    private Label eemaldatavadLabel;
    @FXML
    private Button laeEelnevPuu;

    @FXML
    private Button lisaVasakAlluv;
    @FXML
    private Label juhend;
    private Kahendotsimispuu puu;
    private Kahendotsimispuu visuaalnePuu;
    private Kahendotsimispuu eelnevaSeisugaPuu;
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<Tipp> aktiivsedTipud = new ArrayList<>();
    private List<Integer> eemaldatavad=new ArrayList<>();
    private List<Tipp> metsaJuurtipud = new ArrayList<>();
    private  List<String> vead = new ArrayList<>();
    private int vigu;
    private int hetkelEemaldatav;
    private int puuElementideArv;

    private final String logiFail = "elemendi_eemaldamine_logi.txt";
    private final String sisendFail = "sisendid/elemendiEemaldamine.txt";


    public void laePuu() {
        juhend.setText("Kasutusjuhend:\n" +
                "- Kontrolli eemaldust: Kontrollib tipu eemaldust ja võtab eemaldatavate järjendist uue eemaldatava.\n" +
                "- Lae eelmine puu olek: Laeb viimati kontrollitud puu.\n" +
                "- Rohelise tipu kirjet saab muuta kui ta on ainukene aktiivne tipp.\n" +
                "- Lisa vasak/parem alluv: Lisab punase tipu rohelise tipu vasakuks/paremaks alluvaks.\n");
        lisaVasakAlluv.setVisible(false);
        lisaParemAlluv.setVisible(false);
        kustutaTipp.setVisible(false);
        laeEelnevPuu.setVisible(true);
        kahendpuuAla.getChildren().clear();
        eemaldatavad.clear();
        visuaalsedTipud.clear();
        aktiivsedTipud.clear();
        metsaJuurtipud.clear();
        vigu=0;
        puu = new Kahendotsimispuu();
        visuaalnePuu = new Kahendotsimispuu();
        vead=new ArrayList<>();
        //Logidesse sisend loetakse failist lugemisel
        loeFailistVäärtused(sisendFail);

        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
        uuendaNooli();
        metsaJuurtipud.add(visuaalnePuu.juurtipp);

        järgmineEemaldatav();
        laeUusPuu.setVisible(false);
    }
    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();
        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
    }
    public void laeEelnevPuu(){
        visuaalnePuu = new Kahendotsimispuu();
        puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
        visuaalsedTipud.clear();
        aktiivsedTipud.clear();
        metsaJuurtipud.clear();
        metsaJuurtipud.add(visuaalnePuu.juurtipp);
        ilusPuu();
        uuendaNooli();

        laeEelnevPuu.setVisible(false);
        lukustaPuu.setVisible(false);
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

    private void loeFailistVäärtused(String failitee) {
        try {
            List<String> sisu = Files.readAllLines(Path.of(failitee));
            String järjendid = sisu.get(0);

            //logi
            vead.add("Sisend:\n" + järjendid);

            int puuIndeks = järjendid.indexOf("[");
            int puuLõppIndeks = järjendid.indexOf("]");
            for (String väärtus : järjendid.substring(puuIndeks+1, puuLõppIndeks).split(",")) {
                puu.lisa(new Tipp(Integer.parseInt(väärtus.strip())), false);
                visuaalnePuu.lisa(new Tipp(Integer.parseInt(väärtus.strip())), false);
            }

            String järjend2 = järjendid.substring(puuLõppIndeks+1);
            int eemaldatavadIndeks = järjend2.indexOf("[");
            int eemaldatavadLõppIndeks = järjend2.indexOf("]");

            for (String väärtus : järjend2.substring(eemaldatavadIndeks+1, eemaldatavadLõppIndeks).split(",")) {
                eemaldatavad.add(Integer.parseInt(väärtus.strip()));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void järgmineEemaldatav(){
        lukustaPuu.setVisible(false);
        laeEelnevPuu.setVisible(false);
        if (!eemaldatavad.isEmpty()) {

            eelnevaSeisugaPuu = new Kahendotsimispuu();
            puudSamaks(eelnevaSeisugaPuu, puu.juurtipp);
            puuElementideArv = puu.puuElementideArv(puu.juurtipp);
            puu.eemaldaTipp(puu.juurtipp, eemaldatavad.get(0), false);

            puu.printPuuJaVisuaalnePuu(puu.juurtipp);

            eemaldatav.setText("Eemalda kahendotsingupuust tipp: " + eemaldatavad.get(0));
            eemaldatavadLabel.setText("Järgmised eemaldatavad: " + eemaldatavad.subList(1, eemaldatavad.size()));
            hetkelEemaldatav = eemaldatavad.get(0);
            eemaldatavad.remove(0);
        } else {
            eemaldatav.setText("Kõik tipud eemaldatud!");
            uuendaNooli();
            kustutaTipp.setVisible(false);
            Logija.logiViga(vead, logiFail);
            kuvaTeade("Läbimäng tehtud", "Vigu kokku: " + vigu);
            laeUusPuu.setVisible(true);
        }

    }
    public Group liigutatavTipp(Tipp tipp, boolean vasak) {
        VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        Text tekst = new Text(visuaalneTipp.tipp==null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

        tekst.setLayoutX(visuaalneTipp.getCenterX() - 4);
        tekst.setLayoutY(visuaalneTipp.getCenterY() + 4);
        Group grupp = new Group(visuaalneTipp, tekst);

        grupp.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            Tipp vanem = null;
            Tipp juur = null;
            for (Tipp j : metsaJuurtipud){
                vanem = visuaalnePuu.getVanemKahendpuu(j, tipp);
                juur = j;
                if (vanem != null)
                    break;
            }
            if (vanem==null) {
                vanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                juur = visuaalnePuu.juurtipp;
            }
            //if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp) == null) return;
            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40) return;
            //if (e.getY() < 35 || e.getY() > kahendpuuAla.getHeight() - 35) return;
            if (vanem != null) {
                if (tipp != juur && vasak && e.getX() > vanem.visuaalneTipp.getCenterX() - tipuRaadius) return;

                if (tipp != juur && !vasak && e.getX() < vanem.visuaalneTipp.getCenterX() + tipuRaadius) return;
            }
            /*else if (tipp!=visuaalnePuu.juurtipp && vasak && e.getX() > vanem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            else if (tipp!=visuaalnePuu.juurtipp && !vasak && e.getX() < vanem.visuaalneTipp.getCenterX()+tipuRaadius) return;*/

            if (tipp.vasak!=null && tipp.vasak.visuaalneTipp!=null && e.getX() < tipp.vasak.visuaalneTipp.getCenterX()+tipuRaadius) return;
            else if (tipp.parem!=null && tipp.parem.visuaalneTipp!=null && e.getX() > tipp.parem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            visuaalneTipp.setCenterX(e.getX());
            tekst.setLayoutX(e.getX() - 4);

            uuendaNooli();
        });


        grupp.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 1) {
                StringBuilder inputText = new StringBuilder(tekst.getText());
                //nuppude asukohad
                if (laeEelnevPuu.isVisible()){
                    lisaVasakAlluv.setLayoutX(143);
                    lisaParemAlluv.setLayoutX(143);
                }
                else{
                    lisaVasakAlluv.setLayoutX(10);
                    lisaParemAlluv.setLayoutX(10);
                }


                if ((visuaalneTipp.getFill()==Color.GREEN || visuaalneTipp.getFill()==Color.RED)){
                    if(aktiivsedTipud.size()==1) {
                        kustutaTipp.setVisible(false);
                    }else if (aktiivsedTipud.size()==2 && visuaalneTipp.getFill()==Color.RED &&
                            puuElementideArv == visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp)) {
                        kustutaTipp.setVisible(true);
                    }
                    aktiivsedTipud.remove(tipp);
                    visuaalneTipp.setFill(Color.GREY);
                    lisaVasakAlluv.setVisible(false);
                    lisaParemAlluv.setVisible(false);
                    return;
                }
                if (aktiivsedTipud.size()<2){

                    aktiivsedTipud.add(tipp);
                    if (aktiivsedTipud.size()==1) {
                        visuaalneTipp.setFill(Color.GREEN);
                        if (puuElementideArv == visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp)) {
                            kustutaTipp.setVisible(true);
                        }
                    }
                    else if (aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN) {
                        visuaalneTipp.setFill(Color.RED);
                        lisaVasakAlluv.setVisible(true);
                        lisaParemAlluv.setVisible(true);
                        kustutaTipp.setVisible(false);
                    }
                    else {
                        visuaalneTipp.setFill(Color.GREEN);
                        kustutaTipp.setVisible(false);
                        lisaVasakAlluv.setVisible(true);
                        lisaParemAlluv.setVisible(true);
                    }
                }
                else if (aktiivsedTipud.size()==2) {
                    visuaalneTipp.setFill(aktiivsedTipud.get(0).visuaalneTipp.getFill());

                    aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                    aktiivsedTipud.remove(aktiivsedTipud.get(0));

                    aktiivsedTipud.add(tipp);
                }

                grupp.requestFocus();
                grupp.addEventHandler(KeyEvent.KEY_TYPED, keyEvent ->  {
                    if (visuaalneTipp.getFill()==Color.GREEN) {
                        uuendaNooli();
                        String input = keyEvent.getCharacter();
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

                            aktiivsedTipud.remove(tipp);

                            keyEvent.consume();
                        }
                    }
                });

                kustutaTipp.setOnAction(e2 -> {
                    if (tipp==visuaalnePuu.juurtipp) {
                        vigu++;
                        vead.add("ALGORITMILINE VIGA: eemaldada üritati juurtippu. Eemaldatav tipp oli: " + eemaldatavad.get(0));
                        kuvaTeade("","Juurtippu kustutada ei saa, aga saab muuta väärtust");
                        return;
                    }
                    if (tipp.parem!=null && tipp.vasak!=null){
                        kuvaTeade("","Ära eemalda kahe alluvaga tippu vaid muuda kirjeid");
                        return;
                    }
                    /*if (visuaalneTipp.getFill()==Color.GREEN && aktiivsedTipud.size()==1) {
                        kahendpuuAla.getChildren().remove(grupp);
                        visuaalsedTipud.remove(visuaalneTipp);
                        visuaalneTipp.tipp = null;
                        aktiivsedTipud.remove(tipp);

                        Tipp vanem =visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                        if (vanem!=null && vanem.parem == tipp) {
                            vanem.parem = null;
                        } else if (vanem!=null){
                            vanem.vasak = null;
                        }
                        uuendaNooli();
                        kustutaTipp.setVisible(false);
                    }*/ else if (aktiivsedTipud.size()==1 && aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN) {
                        välimine:
                        for (Node n : kahendpuuAla.getChildren()){
                            if (n instanceof Group group){
                                for (Node c : group.getChildren()){
                                    if (c instanceof VisuaalneTipp && ((VisuaalneTipp) c).getFill()==Color.GREEN){
                                        if (((VisuaalneTipp) c).tipp==visuaalnePuu.juurtipp) {
                                            vigu++;
                                            vead.add("ALGORITMILINE VIGA: eemaldada üritati juurtippu. Eemaldatav tipp oli: " + eemaldatavad.get(0));
                                            kuvaTeade("","Juurtippu kustutada ei saa, aga saab muuta väärtust");
                                            return;
                                        }
                                        if (((VisuaalneTipp) c).tipp.parem!=null && ((VisuaalneTipp) c).tipp.vasak!=null){
                                            kuvaTeade("","Ära eemalda kahe alluvaga tippu vaid muuda kirjeid");
                                            return;
                                        }
                                        kahendpuuAla.getChildren().remove(group);
                                        visuaalsedTipud.remove(c);

                                        aktiivsedTipud.clear();

                                        Tipp metsaJuur;
                                        if (((VisuaalneTipp) c).tipp.parem != null){
                                            metsaJuur = ((VisuaalneTipp) c).tipp.parem;
                                            metsaJuur.tase = visuaalnePuu.leiaTipuTase(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp.parem);
                                            metsaJuurtipud.add(metsaJuur);
                                        }
                                        if (((VisuaalneTipp) c).tipp.vasak != null){
                                            metsaJuur = ((VisuaalneTipp) c).tipp.vasak;
                                            metsaJuur.tase = visuaalnePuu.leiaTipuTase(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp.vasak);
                                            metsaJuurtipud.add(metsaJuur);
                                        }
                                        Tipp vanem =visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp);
                                        if (vanem!=null && vanem.parem == ((VisuaalneTipp) c).tipp) {
                                            vanem.parem = null;
                                        } else if (vanem!=null){
                                            vanem.vasak = null;
                                        }
                                        /*if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp)!=null && visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp,((VisuaalneTipp) c).tipp).parem == ((VisuaalneTipp) c).tipp) {
                                            visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp).parem = null;
                                        } else if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp)!=null){
                                            visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp).vasak = null;
                                        }*/
                                        uuendaNooli();
                                        kustutaTipp.setVisible(false);

                                        break välimine;
                                    }
                                }
                            }
                        }
                    }
                    laeEelnevPuu.setVisible(true);
                    lukustaPuu.setVisible(true);

                });
                lisaVasakAlluv.setOnAction(e3 ->{
                        lisaVasakAlluv();
                        uuendaNooli();
                        lisaVasakAlluv.setVisible(false);
                        lisaParemAlluv.setVisible(false);
                });
                lisaParemAlluv.setOnAction(e4 -> {
                        lisaParemAlluv();
                        uuendaNooli();
                        lisaVasakAlluv.setVisible(false);
                        lisaParemAlluv.setVisible(false);
                });

                e.consume();
            }
        });

        return grupp;
    }
    private void lisaVasakAlluv(){
        Tipp kust = null;
        Tipp kuhu = null;

        boolean vasak = false;
        Tipp kuhuVanem = null;

        for (Tipp tipp : aktiivsedTipud){
            if (tipp.visuaalneTipp.getFill()==Color.GREEN){
                kust = tipp;
            }else {
                kuhu = tipp;

                if (kuhu==visuaalnePuu.juurtipp){
                    kuvaTeade("","Juurtippu ei saa alluvaks määrata");
                    vigu++;
                    vead.add("ALGORITMILINE VIGA: Juurtipp üritati alluvaks määrata");
                    return;
                }

                for (Tipp juur : metsaJuurtipud){
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(juur, tipp);
                    if (kuhuVanem != null)
                        break;
                }
                if (kuhuVanem==null)
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);

                else {
                    if (kuhu == kuhuVanem.vasak)
                        vasak = true;
                }
            }
        }

        if(visuaalnePuu.kasTippOnSamasHarus(kuhu, kust)){
            kuvaTeade("","Ülemtippu ei saa alluvaks määrata");
        } else if (kust.vasak != null) {
            kuvaTeade("", "Tipul on vasak alluv juba olemas");
        } else if (kuhu == visuaalnePuu.juurtipp) {
            vigu++;
            vead.add("ALGORITMILINE VIGA: Juurtipp üritati alluvaks määrata");
            kuvaTeade("","Juurtippu ei saa alluvaks määrata");
        } else {

            kust.vasak = kuhu;
            if (kuhuVanem!=null && vasak)
                kuhuVanem.vasak = null;
            else if (kuhuVanem != null)
                kuhuVanem.parem = null;
            visuaalsedTipud.clear();

            kahendpuuAla.getChildren().clear();
            looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
            metsaJuurtipud.remove(kuhu);
            for (Tipp t : metsaJuurtipud){
                if (t != visuaalnePuu.juurtipp) {
                    looVisuaalnePuu(t, t.tase, (int) t.visuaalneTipp.getCenterX(), (int) t.visuaalneTipp.getCenterY(), true);
                }
            }
            //ilusPuu();
            aktiivsedTipud.clear();
        }
    }
    private void lisaParemAlluv(){
        Tipp kust = null;
        Tipp kuhu = null;

        boolean vasak = false;
        Tipp kuhuVanem = null;

        for (Tipp tipp : aktiivsedTipud){
            if (tipp.visuaalneTipp.getFill()==Color.GREEN){
                kust = tipp;
            }else {
                kuhu = tipp;

                if (kuhu==visuaalnePuu.juurtipp){
                    vigu++;
                    kuvaTeade("","Juurtippu ei saa alluvaks määrata");
                    vead.add("ALGORITMILINE VIGA: Juurtipp üritati alluvaks määrata");
                    return;
                }
                for (Tipp juur : metsaJuurtipud){
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(juur, tipp);
                    if (kuhuVanem != null)
                        break;
                }
                if (kuhuVanem==null)
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                else {
                    if (kuhu == kuhuVanem.vasak)
                        vasak = true;
                }
            }
        }
        if(visuaalnePuu.kasTippOnSamasHarus(kuhu, kust)){
            kuvaTeade("","Tipu ülemat tippu ei saa alluvaks määrata");
        } else if (kust.parem != null) {
            kuvaTeade("", "Tipul on parem alluv juba olemas");
        } else if (kuhu == visuaalnePuu.juurtipp) {
            vigu++;
            vead.add("ALGORITMILINE VIGA: Juurtipp üritati alluvaks määrata");
            kuvaTeade("","Juurtippu ei saa alluvaks määrata");
        }
        else {
            kust.parem = kuhu;
            if (kuhuVanem!=null && vasak)
                kuhuVanem.vasak = null;
            else if (kuhuVanem != null)
                kuhuVanem.parem = null;
            visuaalsedTipud.clear();
            kahendpuuAla.getChildren().clear();
            looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
            metsaJuurtipud.remove(kuhu);

            for (Tipp t : metsaJuurtipud){
                if (t != visuaalnePuu.juurtipp) {
                    looVisuaalnePuu(t, t.tase, (int) t.visuaalneTipp.getCenterX(), (int) t.visuaalneTipp.getCenterY(), true);
                }
            }
            //ilusPuu();
            aktiivsedTipud.clear();
        }
    }
    private void uuendaNooli(){
        kahendpuuAla.getChildren().removeIf(e -> e instanceof Arrow);
        List<Arrow> nooled = new ArrayList<>();
        for (VisuaalneTipp vt : visuaalsedTipud) {
            if (vt.tipp.parem != null && vt.tipp.parem.visuaalneTipp.tipp!=null ) {//&& visuaalsedTipud.contains(vt.tipp.parem.visuaalneTipp)
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
    public void lukustaPuuOlek(){

        if(visuaalnePuu.kasOnKahendotsimispuu(visuaalnePuu.juurtipp, Integer.MIN_VALUE, Integer.MAX_VALUE, true)
                && puuElementideArv-1 == visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp)) {
            if(kasPuudOnSamad(puu.juurtipp, visuaalnePuu.juurtipp)){
                vead.add(hetkelEemaldatav + " eemaldati korrektselt");
                kuvaTeade("","Korrektne eemaldus!");
            }else {
                vigu++;
                vead.add("VIGA: " + hetkelEemaldatav + " eemaldati ebakorrektselt, säilis kahendotsimispuu");
                kuvaTeade("","Ebakorrektne eemaldus, kuid on säilitatud kahendotsimispuu");

                puu = new Kahendotsimispuu();
                puudSamaks(puu, visuaalnePuu.juurtipp);

            }
            järgmineEemaldatav();
        }else {
            vigu++;
            vead.add("VIGA: " + hetkelEemaldatav + " eemaldati ebakorrektselt, kahendotsimispuu struktuur kaotati");
            kuvaTeade("","Ebakorrektne eemaldus ja puu ei ole enam Kahendotsimispuu!");

            visuaalnePuu = new Kahendotsimispuu();
            puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
            visuaalsedTipud.clear();
            aktiivsedTipud.clear();
            ilusPuu();
            uuendaNooli();
            lisaVasakAlluv.setVisible(false);
            lisaParemAlluv.setVisible(false);
        }
        laeEelnevPuu.setVisible(false);
        lukustaPuu.setVisible(false);
    }
    private void puudSamaks(Kahendotsimispuu p, Tipp vTipp){
        if(vTipp==null)
            return;
        p.lisa(new Tipp(vTipp.väärtus), false);
        puudSamaks(p, vTipp.vasak);
        puudSamaks(p, vTipp.parem);

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
    private void kuvaTeade(String pealkiri, String sisu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(kahendpuuAla.getScene().getWindow());
        alert.setTitle("Teavitus");
        alert.setHeaderText(pealkiri);
        alert.setContentText(sisu);
        alert.showAndWait();
    }
}
