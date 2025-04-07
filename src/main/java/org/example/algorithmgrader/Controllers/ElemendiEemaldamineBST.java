package org.example.algorithmgrader.Controllers;

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
    private Button eemaldaTippNupp;
    @FXML
    private Button laeUusPuu;
    @FXML
    private Button lukustaPuu;
    @FXML
    private Button kustutaTipp;
    @FXML
    private Button lisaParemAlluv;
    @FXML
    private Button eelnevaLukustusePuu;
    @FXML
    private Label eemaldatav;
    @FXML
    private Label eemaldatavadLabel;

    @FXML
    private Button lisaVasakAlluv;
    @FXML
    private Label juhend;
    private Kahendotsimispuu puu;
    private Kahendotsimispuu visuaalnePuu;
    private Kahendotsimispuu eelnevaSeisugaPuu;
    private List<VisuaalneTipp> visuaalsedTipud = new ArrayList<>();
    private List<Tipp> aktiivsedTipud = new ArrayList<>();
    private boolean hetkelMuudetakseTippu = false;
    private List<Integer> eemaldatavad=new ArrayList<>();
    private int puuElementideArv;

    private final int JUURE_X = 400;
    private final int JUURE_Y = 50;
    private final int tipuRaadius = 20;
    private final int pesaX = 40;
    private final int pesaY = 60;
    private  final int pesaRaadius = 15;


    public void laePuu() {
        juhend.setText("Kasutusjuhend:\n" +
                "- Lukusta puu olek: Kontrollib kas tipp on eemaldatud korrektselt\n    ja võtab eemaldatavate järjendist uue tipu.\n" +
                "- Lae uus puu" + ": Algatab uue puu.\n" +
                "- Lae eelmine puu olek: Laeb viimati lukustatud puu.\n" +
                "- 1 klikk tipul: Muudab tipu aktiivseks, korraga saab valida kokku 2 tippu.\n" +
                "- Lisa vasak/parem alluv: Lisab punase tipu rohelise vasakuks/paremaks alluvaks.\n");
        lisaVasakAlluv.setVisible(false);
        lisaParemAlluv.setVisible(false);
        kustutaTipp.setVisible(false);
        kahendpuuAla.getChildren().clear();
        eemaldatavad.clear();
        visuaalsedTipud.clear();
        puu = new Kahendotsimispuu();
        visuaalnePuu = new Kahendotsimispuu();

        loeFailistVäärtused("sisendid/elemendiEemaldamine.txt");

        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
        uuendaNooli();

        järgmineEemaldatav();
    }
    public void ilusPuu(){
        kahendpuuAla.getChildren().clear();
        looVisuaalnePuu(visuaalnePuu.juurtipp, 1, JUURE_X, JUURE_Y, true);
    }
    public void laeEelnevPuu(){
        visuaalnePuu = new Kahendotsimispuu();
        puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
        visuaalsedTipud.clear();
        ilusPuu();
        uuendaNooli();
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
            int puuIndeks = järjendid.indexOf("[");
            int puuLõppIndeks = järjendid.indexOf("]");
            for (String väärtus : järjendid.substring(puuIndeks+1, puuLõppIndeks).split(",\s++")) {
                puu.lisa(new Tipp(Integer.parseInt(väärtus)), false);
                visuaalnePuu.lisa(new Tipp(Integer.parseInt(väärtus)), false);
            }

            String järjend2 = järjendid.substring(puuLõppIndeks+1);
            int eemaldatavadIndeks = järjend2.indexOf("[");
            int eemaldatavadLõppIndeks = järjend2.indexOf("]");

            for (String väärtus : järjend2.substring(eemaldatavadIndeks+1, eemaldatavadLõppIndeks).split(",\s++")) {
                eemaldatavad.add(Integer.parseInt(väärtus));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void järgmineEemaldatav(){
        if (!eemaldatavad.isEmpty()) {
            eelnevaSeisugaPuu = new Kahendotsimispuu();
            puudSamaks(eelnevaSeisugaPuu, puu.juurtipp);
            puuElementideArv = puu.puuElementideArv(puu.juurtipp);
            puu.eemaldaTipp(puu.juurtipp, eemaldatavad.get(0));

            puu.printPuuJaVisuaalnePuu(puu.juurtipp);

            eemaldatav.setText("Eemalda kahendotsingupuust tipp: " + eemaldatavad.get(0));
            eemaldatavadLabel.setText("Järgmised eemaldatavad: " + eemaldatavad.subList(1, eemaldatavad.size()));
            eemaldatavad.remove(0);
        } else {
            eemaldatav.setText("Kõik tipud eemaldatud!");
            //logiViga("Vigu: " + vigu + "\n");
            uuendaNooli();
            kustutaTipp.setVisible(false);
            kuvaTeade("Läbimäng tehtud", "Vigu kokku: ");
        }

    }
    public Group liigutatavTipp(Tipp tipp, boolean vasak) {
        VisuaalneTipp visuaalneTipp = tipp.visuaalneTipp;
        Text tekst = new Text(visuaalneTipp.tipp==null ? "" : String.valueOf(visuaalneTipp.tipp.getVäärtus()));

        tekst.setLayoutX(visuaalneTipp.getCenterX() - 4);
        tekst.setLayoutY(visuaalneTipp.getCenterY() + 4);
        Group grupp = new Group(visuaalneTipp, tekst);

        grupp.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp) == null) return;
            if (e.getX() < kahendpuuAla.getLayoutX() + 40 || e.getX() > kahendpuuAla.getLayoutX() + kahendpuuAla.getWidth() - 40) return;
            //if (e.getY() < 35 || e.getY() > kahendpuuAla.getHeight() - 35) return;

            if (tipp!=visuaalnePuu.juurtipp && vasak && e.getX() > visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX()-tipuRaadius) return;

            else if (tipp!=visuaalnePuu.juurtipp && !vasak && e.getX() < visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp).visuaalneTipp.getCenterX()+tipuRaadius) return;

            if (tipp.vasak!=null && tipp.vasak.visuaalneTipp!=null && e.getX() < tipp.vasak.visuaalneTipp.getCenterX()+tipuRaadius) return;
            else if (tipp.parem!=null && tipp.parem.visuaalneTipp!=null && e.getX() > tipp.parem.visuaalneTipp.getCenterX()-tipuRaadius) return;

            visuaalneTipp.setCenterX(e.getX());
            //visuaalneTipp.setCenterY(e.getY());
            tekst.setLayoutX(e.getX() - 4);
            //tekst.setY(e.getY() + 4);
            uuendaNooli();
        });


        grupp.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 1) {
                StringBuilder inputText = new StringBuilder(tekst.getText());
                if(hetkelMuudetakseTippu){
                    visuaalneTipp.väärtus=(Integer.parseInt(inputText.toString()));
                    tipp.väärtus=(Integer.parseInt(inputText.toString()));
                    visuaalneTipp.setFill(Color.GRAY);
                    hetkelMuudetakseTippu=false;
                    aktiivsedTipud.clear();
                    kustutaTipp.setVisible(false);
                    return;
                }
                if ((visuaalneTipp.getFill()==Color.GREEN || visuaalneTipp.getFill()==Color.RED)/*&&!hetkelMuudetakseTippu*/){
                    if(aktiivsedTipud.size()==1) {
                        kustutaTipp.setVisible(false);
                    }else if (aktiivsedTipud.size()==2 && visuaalneTipp.getFill()==Color.RED && puuElementideArv == visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp)) {
                        kustutaTipp.setVisible(true);
                    }
                    aktiivsedTipud.remove(tipp);
                    visuaalneTipp.setFill(Color.GREY);
                    lisaVasakAlluv.setVisible(false);
                    lisaParemAlluv.setVisible(false);
                    return;
                }
                if (aktiivsedTipud.size()<2 && !hetkelMuudetakseTippu){
                    aktiivsedTipud.add(tipp);
                    if (aktiivsedTipud.size()==1) {
                        visuaalneTipp.setFill(Color.GREEN);
                        if (puuElementideArv == visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp))
                            kustutaTipp.setVisible(true);
                    } else if (aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN) {
                        visuaalneTipp.setFill(Color.RED);
                        lisaVasakAlluv.setVisible(true);
                        lisaParemAlluv.setVisible(true);
                        kustutaTipp.setVisible(false);
                    } else {
                        visuaalneTipp.setFill(Color.GREEN);
                        kustutaTipp.setVisible(false);
                        lisaVasakAlluv.setVisible(true);
                        lisaParemAlluv.setVisible(true);
                    }
                }else{
                    kuvaTeade("Info", "Maksimaalselt korraga saab valida 2 tippu!");
                    return;
                }
                grupp.requestFocus();
                /*if(aktiivsedTipud.size()==1) {
                    visuaalneTipp.setFill(Color.GREEN);
                    if (puuElementideArv == visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp))
                        kustutaTipp.setVisible(true);
                } else if (aktiivsedTipud.size()==2 && aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN) {
                    visuaalneTipp.setFill(Color.RED);
                    lisaVasakAlluv.setVisible(true);
                    lisaParemAlluv.setVisible(true);
                    kustutaTipp.setVisible(false);
                }else {
                    visuaalneTipp.setFill(Color.GREEN);
                    lisaVasakAlluv.setVisible(true);
                    lisaParemAlluv.setVisible(true);
                    kustutaTipp.setVisible(false);
                }*/

                grupp.addEventHandler(KeyEvent.KEY_TYPED, keyEvent ->  {
                        if (visuaalneTipp.getFill()==Color.GREEN) {
                            hetkelMuudetakseTippu = true;
                            String input = keyEvent.getCharacter();
                            if (input.matches("\\d") && inputText.length() < 3) {
                                inputText.append(input);
                                tekst.setText(inputText.toString());
                            } else if (input.matches("\b") && !inputText.isEmpty()) {
                                inputText.deleteCharAt(inputText.length() - 1);
                                tekst.setText(inputText.toString());
                            } else if (input.equals("\r") || input.equals("\n")) {
                                if (!inputText.isEmpty()) {
                                    visuaalneTipp.väärtus = (Integer.parseInt(inputText.toString()));
                                    tipp.väärtus = (Integer.parseInt(inputText.toString()));
                                    visuaalneTipp.setFill(Color.GRAY);
                                }
                                // grupp.removeEventHandler(KeyEvent.KEY_TYPED, keyEvent);
                                hetkelMuudetakseTippu = false;
                                aktiivsedTipud.remove(tipp);
                                kustutaTipp.setVisible(false);
                                keyEvent.consume();
                            }
                        }

                });
                kustutaTipp.setOnAction(e2 -> {
                    if (tipp==visuaalnePuu.juurtipp) {
                        kuvaTeade("","Juurtippu kustutada ei saa, aga saab muuta väärtust");
                        Logija.logiViga("Üritati eemaldad juurtippu", "elemendi_eemaldamine");
                        return;
                    }
                    if (tipp.parem!=null && tipp.vasak!=null){
                        kuvaTeade("","Ära eemalda kahe alluvaga tippu vaid muuda kirjeid");
                        return;
                    }
                    if (visuaalneTipp.getFill()==Color.GREEN && aktiivsedTipud.size()==1) {
                        kahendpuuAla.getChildren().remove(grupp);
                        visuaalsedTipud.remove(visuaalneTipp);
                        visuaalneTipp.tipp = null;
                        aktiivsedTipud.remove(tipp);

                        if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp)!=null && visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp).parem == tipp) {
                            visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp).parem = null;
                        } else if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp)!=null){
                            visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp).vasak = null;
                        }
                        uuendaNooli();
                        kustutaTipp.setVisible(false);
                    } else if (aktiivsedTipud.size()==1 && aktiivsedTipud.get(0).visuaalneTipp.getFill()==Color.GREEN) {
                        välimine:
                        for (Node n : kahendpuuAla.getChildren()){
                            if (n instanceof Group group){
                                for (Node c : group.getChildren()){
                                    if (c instanceof VisuaalneTipp && ((VisuaalneTipp) c).getFill()==Color.GREEN){
                                        if (((VisuaalneTipp) c).tipp==visuaalnePuu.juurtipp) {
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

                                        if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp)!=null && visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp,((VisuaalneTipp) c).tipp).parem == ((VisuaalneTipp) c).tipp) {
                                            visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp).parem = null;
                                        } else if (visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp)!=null){
                                            visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, ((VisuaalneTipp) c).tipp).vasak = null;
                                        }
                                        uuendaNooli();
                                        kustutaTipp.setVisible(false);
                                        break välimine;
                                    }
                                }
                            }
                        }
                    }

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
                    kuvaTeade("","Ei saa juurtippu alluvaks määrata");
                    Logija.logiViga("Üritati juurtippu alluvaks määrata","elemendi_eemaldamine");
                    return;
                }
                kuhuVanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                if (kuhuVanem!=null) {
                    if (kuhu == kuhuVanem.vasak)
                        vasak = true;
                }
            }
        }

        if(visuaalnePuu.kasTippOnSamasHarus(kuhu, kust)){
            kuvaTeade("","Tipu ülemat tippu ei saa alluvaks määrata");
        } else if (kust.vasak != null) {
            kuvaTeade("", "Tipul on vasak alluv juba olemas");
        } else if (kuhu == visuaalnePuu.juurtipp) {
            kuvaTeade("","Juurtippu ei saa alluvaks lisada");
        } else {
            kust.vasak = kuhu;
            if (kuhuVanem!=null && vasak)
                kuhuVanem.vasak = null;
            else if (kuhuVanem != null)
                kuhuVanem.parem = null;
            visuaalsedTipud.clear();
            ilusPuu();
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
                    kuvaTeade("","Ei saa juurtippu alluvaks määrata");
                    Logija.logiViga("Üritati juurtippu alluvaks määrata","elemendi_eemaldamine");
                    return;
                }
                kuhuVanem = visuaalnePuu.getVanemKahendpuu(visuaalnePuu.juurtipp, tipp);
                if (kuhuVanem!=null) {
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
            kuvaTeade("","Juurtippu ei saa alluvaks lisada");
        }
        else {
            kust.parem = kuhu;
            if (kuhuVanem!=null && vasak)
                kuhuVanem.vasak = null;
            else if (kuhuVanem != null)
                kuhuVanem.parem = null;
            visuaalsedTipud.clear();
            ilusPuu();
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
            }else if(vt.tipp.parem!=null &&vt.tipp.parem.visuaalneTipp!=null){

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
        if (puuElementideArv-1 != visuaalnePuu.puuElementideArv(visuaalnePuu.juurtipp)){
            kuvaTeade("","Enne tipu eemaldamist ei saa olekut lukku panna");
            return;
        }
        if(visuaalnePuu.kasOnKahendotsimispuu(visuaalnePuu.juurtipp, Integer.MIN_VALUE, Integer.MAX_VALUE, true)) {
            if(kasPuudOnSamad(puu.juurtipp, visuaalnePuu.juurtipp)){
                System.out.println("\n");
                puu.printPuuJaVisuaalnePuu(puu.juurtipp);
                System.out.println("\n");
                visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
                kuvaTeade("","Korrektne eemaldus!");
                järgmineEemaldatav();
            }else {
                System.out.println("\n");
                puu.printPuuJaVisuaalnePuu(puu.juurtipp);
                System.out.println("\n");
                visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
                kuvaTeade("","Ebakorrektne eemaldus, kuid on säilitatud kahendotsimispuu");
                Logija.logiViga("Eemaldus ebakorrektne, kahendotsimispuu säilis\n", "elemendi_eemaldamine");

                //TODO logi viga
                järgmineEemaldatav();

            }
        }else {
            System.out.println("\n");
            puu.printPuuJaVisuaalnePuu(puu.juurtipp);
            System.out.println("\n");
            visuaalnePuu.printPuuJaVisuaalnePuu(visuaalnePuu.juurtipp);
            kuvaTeade("","Ebakorrektne eemaldus ja puu ei ole enam Kahendotsimispuu!");
            Logija.logiViga("Eemaldus ebakorrektne, andmestruktuur ei ole enam kahendotsimispuu\n", "elemendi_eemaldamine");
            visuaalnePuu = new Kahendotsimispuu();
            puudSamaks(visuaalnePuu, eelnevaSeisugaPuu.juurtipp);
            visuaalsedTipud.clear();
            ilusPuu();
            uuendaNooli();

        }
        System.out.println("\n");

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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(kahendpuuAla.getScene().getWindow());
        alert.setTitle("Teavitus");
        alert.setHeaderText(pealkiri);
        alert.setContentText(sisu);
        alert.showAndWait();
    }
}
