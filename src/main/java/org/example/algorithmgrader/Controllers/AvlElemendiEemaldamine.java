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

public class AvlElemendiEemaldamine {
    @FXML
    private Pane kahendpuuAla;
    @FXML
    private Label eemaldatav;
    @FXML
    private Label eemaldatavLabel;
    @FXML
    private Button lukustaPuu;
    @FXML
    private Button eemaldaVasakAlluv;
    @FXML
    private Button eemaldaParemAlluv;
    @FXML
    private Button lisaParemAlluv;
    @FXML
    private Button lisaVasakAlluv;
    @FXML
    private Button kustutaTipp;
    @FXML
    private Button laeEelnevPuu;
    @FXML
    private Button laeUusPuu;
    @FXML
    private Label juhend;
    private Kahendotsimispuu puu;
    private Kahendotsimispuu visuaalnePuu;
    private Kahendotsimispuu eelnevaSeisugaPuu;
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<Tipp> aktiivsedTipud = new ArrayList<>();
    private List<Integer> eemaldatavad = new ArrayList<>();
    private List<Tipp> metsaJuurtipud = new ArrayList<>();
    private List<String> vead = new ArrayList<>();
    private boolean hetkelMuudetakseTippu = false;
    private boolean eemaldatud;
    private int vigu;
    private final String logiFail = "avl_eemaldamine_logi.txt";
    private final String sisendFail = "sisendid/avlEemaldamine.txt";


    private void loeFailistVäärtusedJaLooAlgnePuu(String failitee) {
        try {
            List<String> sisu = Files.readAllLines(Path.of(failitee));
            String järjendid = sisu.get(0);
            vigu=0;

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
    public void laePuu() {
        juhend.setText("Kasutusjuhend:\n" +
                "- Lukusta puu olek: Kontrollib tipu eemaldust.\n" +
                "- Lae eelmine puu olek: Laeb viimati lukustatud puu.\n" +
                "- Eemalda seos alluvaga: Eemaldab seose vasku/parema alluvaga\n" +
                "- Lisa vasak/parem alluv: Lisab punase tipu rohelise vasakuks/paremaks alluvaks.\n");


        kahendpuuAla.getChildren().clear();
        eemaldatavad.clear();
        visuaalsedTipud.clear();
        aktiivsedTipud.clear();
        metsaJuurtipud.clear();
        puu = new Kahendotsimispuu();
        visuaalnePuu = new Kahendotsimispuu();
        vead=new ArrayList<>();

        loeFailistVäärtusedJaLooAlgnePuu(sisendFail);

        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
        uuendaNooli();
        metsaJuurtipud.add(visuaalnePuu.juurtipp);

        järgmineEemaldatav();
        laeUusPuu.setVisible(false);
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
    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();

        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
    }

    public void järgmineEemaldatav(){
        lukustaPuu.setVisible(false);
        laeEelnevPuu.setVisible(false);
        if (!eemaldatavad.isEmpty()) {
            eelnevaSeisugaPuu = new Kahendotsimispuu();
            puudSamaks(eelnevaSeisugaPuu, puu.juurtipp);

            eemaldatud=false;

            puu.eemaldaTipp(eemaldatavad.get(0), true);

            eemaldatav.setText("Eemalda AVL-puust tipp: " + (eemaldatavad.isEmpty() ? "Kõik tipud eemaldatud!" : eemaldatavad.get(0)));
            eemaldatavLabel.setText(eemaldatavad.subList(1,eemaldatavad.size()).toString());
        } else {
            eemaldatav.setText("Kõik tipud eemaldatud!");
            eemaldatavLabel.setText("");
            Logija.logiViga(vead, logiFail);
            uuendaNooli();
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

            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40) return;
            if (vanem != null) {
                if (tipp != juur && vasak && e.getX() > vanem.visuaalneTipp.getCenterX() - tipuRaadius) return;

                if (tipp != juur && !vasak && e.getX() < vanem.visuaalneTipp.getCenterX() + tipuRaadius) return;
            }

            if (tipp.vasak!=null && tipp.vasak.visuaalneTipp!=null && e.getX() < tipp.vasak.visuaalneTipp.getCenterX()+tipuRaadius) return;
            else if (tipp.parem!=null && tipp.parem.visuaalneTipp!=null && e.getX() > tipp.parem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            visuaalneTipp.setCenterX(e.getX());
            tekst.setLayoutX(e.getX() - 4);

            uuendaNooli();
        });


        grupp.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 1) {
                    StringBuilder inputText = new StringBuilder(tekst.getText());
                    if (laeEelnevPuu.isVisible()){
                        lisaVasakAlluv.setLayoutX(143);
                        lisaParemAlluv.setLayoutX(143);
                        eemaldaParemAlluv.setLayoutX(143);
                        eemaldaVasakAlluv.setLayoutX(143);
                    }
                    else{
                        lisaVasakAlluv.setLayoutX(10);
                        lisaParemAlluv.setLayoutX(10);
                        eemaldaParemAlluv.setLayoutX(10);
                        eemaldaVasakAlluv.setLayoutX(10);
                        kustutaTipp.setLayoutX(190);
                    }

                    if(hetkelMuudetakseTippu){
                        //muudame hetke tipu väärtuse õigeks
                        aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);

                        hetkelMuudetakseTippu = false;
                        aktiivsedTipud.clear();
                    }
                    if ((visuaalneTipp.getFill()==Color.GREEN || visuaalneTipp.getFill()==Color.RED)){
                        aktiivsedTipud.remove(tipp);
                        visuaalneTipp.setFill(Color.GREY);
                        if (aktiivsedTipud.isEmpty()){
                            kustutaTipp.setVisible(false);
                            eemaldaVasakAlluv.setVisible(false);
                            eemaldaParemAlluv.setVisible(false);
                        } else if (aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN){

                            eemaldaVasakAlluv.setVisible(true);
                            eemaldaParemAlluv.setVisible(true);
                            if (!eemaldatud)
                                kustutaTipp.setVisible(true);
                        }
                        lisaVasakAlluv.setVisible(false);
                        lisaParemAlluv.setVisible(false);
                        return;
                    }
                    else if (aktiivsedTipud.size()<2){
                        aktiivsedTipud.add(tipp);
                        if (aktiivsedTipud.size()==1){
                            visuaalneTipp.setFill(Color.GREEN);
                            eemaldaVasakAlluv.setVisible(true);
                            eemaldaParemAlluv.setVisible(true);
                            if (!eemaldatud)
                                kustutaTipp.setVisible(true);
                        }else if (aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN){
                            visuaalneTipp.setFill(Color.RED);
                            lisaVasakAlluv.setVisible(true);
                            lisaParemAlluv.setVisible(true);
                            eemaldaVasakAlluv.setVisible(false);
                            eemaldaParemAlluv.setVisible(false);
                            kustutaTipp.setVisible(false);
                        }else {
                            visuaalneTipp.setFill(Color.GREEN);
                            lisaVasakAlluv.setVisible(true);
                            lisaParemAlluv.setVisible(true);
                            eemaldaVasakAlluv.setVisible(false);
                            eemaldaParemAlluv.setVisible(false);
                            kustutaTipp.setVisible(false);
                        }

                    } else if (aktiivsedTipud.size()==2) {
                        visuaalneTipp.setFill(aktiivsedTipud.get(0).visuaalneTipp.getFill());

                        aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                        aktiivsedTipud.remove(aktiivsedTipud.get(0));

                        aktiivsedTipud.add(tipp);
                    }


                grupp.requestFocus();
                    grupp.addEventHandler(KeyEvent.KEY_TYPED, keyEvent ->  {
                        if (visuaalneTipp.getFill()==Color.GREEN) {
                            hetkelMuudetakseTippu = true;
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
                                hetkelMuudetakseTippu = false;
                                aktiivsedTipud.remove(tipp);

                                keyEvent.consume();
                            }
                        }
                    });
                kustutaTipp.setOnAction(e2 -> {
                    Tipp eemaldatavTipp = aktiivsedTipud.get(0);
                    if (eemaldatavTipp==visuaalnePuu.juurtipp) {
                        kuvaTeade("","Juurtippu kustutada ei saa, aga saab muuta väärtust");
                        return;
                    }

                    if (aktiivsedTipud.size()==1 && aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN) {
                        välimine:
                        for (Node n : kahendpuuAla.getChildren()){
                            if (n instanceof Group group){
                                for (Node c : group.getChildren()){
                                    if (c instanceof VisuaalneTipp && ((VisuaalneTipp) c).getFill()==Color.GREEN){
                                        if (((VisuaalneTipp) c).tipp==visuaalnePuu.juurtipp) {
                                            vigu++;
                                            vead.add("ALGORITMILINE VIGA: üritati puust eemaldada juurtipp");
                                            kuvaTeade("","Juurtippu kustutada ei saa, aga saab muuta väärtust");
                                            return;
                                        }

                                        kahendpuuAla.getChildren().remove(group);
                                        visuaalsedTipud.remove(c);

                                        aktiivsedTipud.clear();

                                        Tipp vanem = null;
                                        for (Tipp j : metsaJuurtipud){
                                            vanem = visuaalnePuu.getVanemKahendpuu(j, ((VisuaalneTipp) c).tipp);
                                            if (vanem != null)
                                                break;
                                        }
                                        if (vanem==null) {
                                            vanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp);
                                        }
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
                                        if (vanem!=null && vanem.parem == ((VisuaalneTipp) c).tipp) {
                                            vanem.parem = null;
                                        } else if (vanem!=null){
                                            vanem.vasak = null;
                                        }
                                        uuendaNooli();
                                        kustutaTipp.setVisible(false);
                                        eemaldatud=true;
                                        break välimine;
                                    }
                                }
                            }
                        }
                    }
                    laeEelnevPuu.setVisible(true);
                    lukustaPuu.setVisible(true);
                    eemaldaParemAlluv.setVisible(false);
                    eemaldaVasakAlluv.setVisible(false);
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

                    eemaldaVasakAlluv.setOnAction(e5 -> {
                        eemaldaSeosVasakuAlluvaga(aktiivsedTipud.get(0));
                        aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                        aktiivsedTipud.remove(aktiivsedTipud.get(0));
                        uuendaNooli();

                        eemaldaVasakAlluv.setVisible(false);
                        eemaldaParemAlluv.setVisible(false);
                        kustutaTipp.setVisible(false);
                    });

                    eemaldaParemAlluv.setOnAction(e5 -> {
                        eemaldaSeosParemaAlluvaga(aktiivsedTipud.get(0));
                        aktiivsedTipud.get(0).visuaalneTipp.setFill(Color.GRAY);
                        aktiivsedTipud.remove(aktiivsedTipud.get(0));
                        uuendaNooli();

                        eemaldaVasakAlluv.setVisible(false);
                        eemaldaParemAlluv.setVisible(false);
                        kustutaTipp.setVisible(false);
                    });

                    e.consume();
                    uuendaNooli();
                }
        });

        return grupp;
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
        eemaldatud=false;

        laeEelnevPuu.setVisible(false);
        lukustaPuu.setVisible(false);
        eemaldaVasakAlluv.setVisible(false);
        eemaldaParemAlluv.setVisible(false);
        lisaVasakAlluv.setVisible(false);
        lisaParemAlluv.setVisible(false);
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

                for (Tipp juur : metsaJuurtipud){
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(juur, tipp);
                    if (kuhuVanem != null)
                        break;
                }
                if (kuhuVanem==null)
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                if (kuhuVanem!=null) {
                    if (kuhu == kuhuVanem.vasak)
                        vasak = true;
                }
            }
        }

        if(visuaalnePuu.kasTippOnSamasHarus(kuhu, kust)){
            kuvaTeade("","Ülemtippu ei saa alluvaks määrata");
        } else if (kust.vasak != null) {
            kuvaTeade("", "Tipul on vasak alluv juba olemas");
        } else {
            for (Tipp juur : metsaJuurtipud){
                if (kuhu == visuaalnePuu.juurtipp && kasTippOnSamasHarus(juur, kust)){
                    visuaalnePuu.juurtipp = juur;
                    break;
                }
                /*if (juur != visuaalnePuu.juurtipp && juur != kuhu && kasTippOnSamasHarus(juur, kuhu) && !kasTippOnSamasHarus(juur, kust)){
                    kuvaTeade("Viga", "Ülemaga tippu ei saa alluvaks lisada");
                    Logija.logiViga("Üritati lisada ülemaga tippu alluvaks", "avl_eemaldamine");
                    return;
                }*/
            }

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
        uuendaNooli();
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

                for (Tipp juur : metsaJuurtipud){
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(juur, tipp);
                    if (kuhuVanem != null)
                        break;
                }
                if (kuhuVanem==null)
                    kuhuVanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                if (kuhuVanem!=null) {
                    if (kuhu == kuhuVanem.vasak)
                        vasak = true;
                }
            }
        }
        if(visuaalnePuu.kasTippOnSamasHarus(kuhu, kust)){
            kuvaTeade("","Ülemtippu ei saa alluvaks määrata");
        } else if (kust.parem != null) {
            kuvaTeade("", "Tipul on parem alluv juba olemas");
        }
        else {
            for (Tipp juur : metsaJuurtipud){
                if (kuhu == visuaalnePuu.juurtipp && kasTippOnSamasHarus(juur, kust)){
                    visuaalnePuu.juurtipp = juur;
                    break;
                }
               /* if (juur != visuaalnePuu.juurtipp && juur != kuhu && kasTippOnSamasHarus(juur, kuhu) && !kasTippOnSamasHarus(juur, kust)){
                    kuvaTeade("Viga", "Ülemaga tippu ei saa alluvaks lisada");
                    Logija.logiViga("Üritati lisada ülemaga tippu alluvaks", "avl_eemaldmine");
                    return;
                }*/
            }

            kust.parem = kuhu;
            if (kuhuVanem!=null && vasak) {
                kuhuVanem.vasak = null;
            }
            else if (kuhuVanem != null) {
                kuhuVanem.parem = null;
            }
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
        uuendaNooli();
    }
    private void eemaldaSeosVasakuAlluvaga(Tipp tipp){
        Tipp uus = tipp.vasak;
        Tipp vanem = null;
        Tipp juur = null;
        for (Tipp j : metsaJuurtipud) {
            vanem = visuaalnePuu.getVanemKahendpuu(j, tipp.vasak);
            juur = j;
            if (vanem != null) {
                //normaalseks puu kuvamiseks
                uus.tase=0;
                break;
            }
        }
        if (vanem==null) {
            juur = visuaalnePuu.juurtipp;
        }
        uus.tase += juur.tase+visuaalnePuu.leiaTipuTase(juur, tipp.vasak);
        tipp.vasak = null;

        metsaJuurtipud.add(uus);
    }
    private void eemaldaSeosParemaAlluvaga(Tipp tipp){
        Tipp uus = tipp.parem;
        Tipp vanem = null;
        Tipp juur = null;
        for (Tipp j : metsaJuurtipud) {
            vanem = visuaalnePuu.getVanemKahendpuu(j, tipp.parem);
            juur = j;
            if (vanem != null) {
                //normaalseks puu kuvamiseks
                uus.tase=0;
                break;
            }
        }
        if (vanem==null) {
            juur = visuaalnePuu.juurtipp;
        }
        uus.tase += juur.tase+visuaalnePuu.leiaTipuTase(juur, tipp.parem);
        tipp.parem = null;

        metsaJuurtipud.add(uus);
    }
    public void lukustaPuuOlek(){
        if (!eemaldatud){
            kuvaTeade("","Enne tipu eemaldamist ei saa puud lukku panna");
            return;
        }
        if (metsaJuurtipud.size()>1){
            vigu++;
            vead.add("VIGA: eemaldamist kontrolliti kui ekraanil oli mitu puud");
            kuvaTeade("","Ei saa olla mitu puud");
            return;
        }
        boolean onBst = visuaalnePuu.kasOnKahendotsimispuu(visuaalnePuu.juurtipp, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        boolean onAvl = visuaalnePuu.kasOnAvl(visuaalnePuu.juurtipp);
        if(onBst && onAvl) {

            if(kasPuudOnSamad(puu.juurtipp, visuaalnePuu.juurtipp)){
                vead.add(eemaldatavad.get(0) + " eemaldati korrektselt");
                kuvaTeade("","Korrektne eemaldamine!");
            }else {
                puu=new Kahendotsimispuu();
                puudSamaks(puu, visuaalnePuu.juurtipp);
                vigu++;
                vead.add("VIGA: " +eemaldatavad.get(0)+ " eemaldati ebakorrektselt, AVL-puu säilis");
                kuvaTeade("","Ebakorrektne eemaldamine, kuid on säilitatud AVL-puu");
            }
            eemaldatavad.remove(0);
            for (Tipp aktiivneTipp : aktiivsedTipud){
                aktiivneTipp.visuaalneTipp.setFill(Color.GRAY);
            }
            aktiivsedTipud.clear();
            eemaldaParemAlluv.setVisible(false);
            eemaldaVasakAlluv.setVisible(false);
            järgmineEemaldatav();
        }else {
            if (onBst){
                vead.add("VIGA: " + eemaldatavad.get(0) + " eemaldati ebakorrektselt, AVL-puu ei säilinud");
                kuvaTeade("","Ebakorrektne eemaldamine ja ei ole säilinud AVL-puu");
            }else {
                vead.add("VIGA: " + eemaldatavad.get(0) + " eemaldati ebakorrektselt, kahendotsimispuu ei säilinud");
                kuvaTeade("","Ebakorrektne eemaldamine ja puu ei ole enam Kahendotsimispuu!");
            }

            vigu++;
            visuaalnePuu = new Kahendotsimispuu();
            puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
            visuaalsedTipud.clear();
            aktiivsedTipud.clear();
            metsaJuurtipud.clear();
            ilusPuu();
            eemaldatud=false;
            eemaldatav.setText("Eemalda AVL-puust tipp: " + eemaldatavad.get(0));
            uuendaNooli();

        }
        laeEelnevPuu.setVisible(false);
        lukustaPuu.setVisible(false);
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
    public boolean kasTippOnSamasHarus(Tipp ulem, Tipp otsitav){
        if (ulem == null)
            return false;

        if (ulem == otsitav)
            return true;

        return kasTippOnSamasHarus(ulem.vasak, otsitav) || kasTippOnSamasHarus(ulem.parem, otsitav);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(kahendpuuAla.getScene().getWindow());
        alert.setTitle("Teavitus");
        alert.setHeaderText(pealkiri);
        alert.setContentText(sisu);
        alert.showAndWait();
    }
}
