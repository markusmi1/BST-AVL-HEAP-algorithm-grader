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
        if (uusTipp.väärtus < tipp.getVäärtus()) {
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
                if (tipp.väärtus<=otsitav.väärtus) {
                    return getVanem(tipp.parem,otsitav);
                }
                else {
                    return getVanem(tipp.vasak,otsitav);
                }
            }
        }
    }
    public Tipp getVanemKahendpuu(Tipp tipp, Tipp otsitav) {
        if (tipp == null || otsitav == juurtipp) {
            return null; // The root node has no parent
        }

        // If left or right child matches the target, return the current node (which is the parent)
        if (tipp.vasak == otsitav || tipp.parem == otsitav) {
            return tipp;
        }

        // Recursively search in the left subtree
        Tipp vasak = getVanemKahendpuu(tipp.vasak, otsitav);
        if (vasak != null) return vasak; // If found in left, return immediately

        // Recursively search in the right subtree
        return getVanemKahendpuu(tipp.parem, otsitav);
    }
    public void printPuuJaVisuaalnePuu(Tipp tipp){
        if (tipp == null)
            return;

        System.out.println("Vaartus "+ tipp.väärtus);
        printPuuJaVisuaalnePuu(tipp.vasak);
        printPuuJaVisuaalnePuu(tipp.parem);

    }
    public int puuElementideArv(Tipp tipp){
        if (tipp==null)
            return 0;
        return 1+puuElementideArv(tipp.vasak)+puuElementideArv(tipp.parem);
    }
    public Tipp eemaldaTipp(Tipp juur, int eemaldatav){
        if (juur == null)
            return null;
        if (juur.väärtus > eemaldatav) {
            juur.vasak=eemaldaTipp(juur.vasak, eemaldatav);
        } else if (juur.väärtus < eemaldatav) {
            juur.parem=eemaldaTipp(juur.parem, eemaldatav);
        } else {
            if (juur.vasak==null && juur.parem==null)
                return null;
            else if (juur.vasak == null) {
                return juur.parem;
            }
            else if (juur.parem == null) {
                return juur.vasak;
            }else {
                Tipp järgnev = leiaJärgnev(juur.parem);
                juur.väärtus = järgnev.väärtus;
                juur.parem = eemaldaTipp(juur.parem, järgnev.väärtus);
            }
        }
        return juur;
    }
    private Tipp leiaJärgnev(Tipp juur) {
        Tipp praegune = juur;
        while (praegune.vasak != null) {
            praegune = praegune.vasak;
        }
        return praegune;
    }
    public boolean kasTippOnSamasHarus(Tipp ülem, Tipp otsitav){
        if (ülem == null)
            return false;

        if (ülem == otsitav)
            return true;

        return kasTippOnSamasHarus(ülem.vasak, otsitav) || kasTippOnSamasHarus(ülem.parem, otsitav);
    }
    public boolean kasOnKahendotsimispuu(Tipp juur, int min, int max, boolean vasak){
        if (juur == null)
            return true;
        boolean v;
        boolean p;

        if (vasak){
            if (juur.väärtus<max && juur.väärtus>=min){
                v = kasOnKahendotsimispuu(juur.vasak, min, juur.väärtus, true);
                p = kasOnKahendotsimispuu(juur.parem, juur.väärtus, max, false);
            }else
                return false;
        }else{
            if(juur.väärtus>=min && juur.väärtus < max) {
                v = kasOnKahendotsimispuu(juur.vasak, min, juur.väärtus, true);
                p = kasOnKahendotsimispuu(juur.parem, juur.väärtus, max,  false);
            }else
                return false;
        }
        return v && p;
    }

    //AVL-puu operatsioonid

}

