package org.example.algorithmgrader.Kahendpuu;

import java.util.ArrayList;
import java.util.List;

public class Kahendotsimispuu {
    public Tipp juurtipp;
    public Tipp viimatiLisatudTipp;
    public List<Tipp> tipud = new ArrayList<>();

    public void lisa(Tipp uusTipp) {
        juurtipp= lisaRekursiivselt(juurtipp, uusTipp);
    }
    private Tipp lisaRekursiivselt(Tipp tipp, Tipp uusTipp) {
        if (tipp == null || tipp.väärtus==-1) {
            viimatiLisatudTipp = uusTipp;
            tipud.add(uusTipp);
            return uusTipp;
        }
        if (uusTipp.väärtus <= tipp.getVäärtus()) {
            tipp.vasak = lisaRekursiivselt(tipp.vasak, uusTipp);
        } else {// (uusTipp.väärtus > tipp.getVäärtus()) {
            tipp.parem = lisaRekursiivselt(tipp.parem, uusTipp);
        }
        return tipp;
    }

    public Tipp getVanem(Tipp tipp, Tipp otsitav) {
        if (otsitav==juurtipp || tipp==null || tipp.väärtus==-1){
            return null;
        }
        else{
            if(tipp.vasak==otsitav || tipp.parem==otsitav)
                return tipp;
            else {
                if (tipp.väärtus<otsitav.väärtus) {
                    return getVanem(tipp.parem,otsitav);
                }
                else {
                    return getVanem(tipp.vasak,otsitav);
                }
            }
        }
    }

    public Tipp getJuurtipp() {
        return juurtipp;
    }

}

