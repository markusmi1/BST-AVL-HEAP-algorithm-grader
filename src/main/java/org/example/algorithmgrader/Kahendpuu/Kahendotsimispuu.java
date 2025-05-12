package org.example.algorithmgrader.Kahendpuu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kahendotsimispuu {
    public Tipp juurtipp;

    public void lisa(Tipp uusTipp, boolean avl) {
        juurtipp = lisaRekursiivselt(juurtipp, uusTipp, avl);
    }

    private Tipp lisaRekursiivselt(Tipp tipp, Tipp uusTipp, boolean avl) {
        if (tipp == null || tipp.väärtus==-1) {

            //tipud.add(uusTipp);
            return uusTipp;
        }
        if (uusTipp.väärtus < tipp.getVäärtus()) {
            tipp.vasak = lisaRekursiivselt(tipp.vasak, uusTipp, avl);
        } else {// (uusTipp.väärtus > tipp.getVäärtus()) {
            tipp.parem = lisaRekursiivselt(tipp.parem, uusTipp, avl);
        }
        return avl ? tasakaalustaPuu(tipp) : tipp;
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
    public void eemaldaTipp(int eemaldatav, boolean avl){
        juurtipp = eemaldaTipp(juurtipp, eemaldatav, avl);
    }
    public Tipp eemaldaTipp(Tipp juur, int eemaldatav, boolean avl){
        if (juur == null)
            return null;
        if (juur.väärtus > eemaldatav) {
            juur.vasak=eemaldaTipp(juur.vasak, eemaldatav, avl);
        } else if (juur.väärtus < eemaldatav) {
            juur.parem=eemaldaTipp(juur.parem, eemaldatav, avl);
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
                juur.parem = eemaldaTipp(juur.parem, järgnev.väärtus, avl);
            }
        }
        return avl ? tasakaalustaPuu(juur) : juur;
    }
    public int leiaTipuTase(Tipp juur, Tipp otsitav){
        if (juur == null)
            return -1;
        if (juur==otsitav)
            return 1;

        if (otsitav.väärtus<juur.väärtus)
            return 1+ leiaTipuTase(juur.vasak, otsitav);
        else
            return 1+ leiaTipuTase(juur.parem, otsitav);
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
    public boolean kasOnAvl(Tipp juur){
        if (juur == null) {
            return true;
        }
        //vasaku ja parema poole kõrguste vahe
        int vahe = kõrgusteVahe(juur);
        //kontrollime, millise pöörde peame sooritama ja sooritame
        if (vahe == 2) {
            return false;
        }
        if (vahe == -2) {
            return false;
        }

        return kasOnAvl(juur.vasak) && kasOnAvl(juur.parem);
    }
    public void listAvlPuuks(List<Integer> list){
        juurtipp=listAvlPuuks(list, 0, list.size()-1);
    }
    public Tipp listAvlPuuks(List<Integer> list, int algus,  int lõpp){
        if (algus > lõpp)
            return null;
        //keskmine element sobib juureks
        int mid = (algus + lõpp) / 2;
        Tipp tipp = new Tipp(list.get(mid));

        tipp.vasak = listAvlPuuks(list, algus, mid - 1);
        tipp.parem = listAvlPuuks(list, mid + 1, lõpp);

        return tipp;
    }

    /**
     * liidab ette antud avl puud
     * @param avl1 ette antud puu  1
     * @param avl2 ette antud puu 2
     * @return tagastab uue avl puu mis koosneb kõigist avl1 ja avl2 elementidest
     */
    public Tipp liidaAVLpuud(Tipp avl1, Tipp avl2) {
        if (avl1 == null) {
            return avl2;
        }
        if (avl2 == null) {
            return avl1;
        }
        //loome keskjärjestuse, et oleks kergem sorteerida
        List<Integer> kesk1 = new ArrayList<>();
        keskjärjestus(avl1, kesk1);
        keskjärjestus(avl2, kesk1);
        //sorteerime listi
        Collections.sort(kesk1);
        //loome avl puu
        Tipp avl = listAvlPuuks(kesk1, 0, kesk1.size()-1);

        return avl;
    }
    /**
     * loob keskjärjestuse listi avl puust
     * @param tipp ette antud avl puu
     * @param list keskjärjestuse hoidmiseks olev list
     */
    public void keskjärjestus(Tipp tipp, List<Integer> list){
        if(tipp == null)
            return;
        keskjärjestus(tipp.vasak, list);
        list.add(tipp.väärtus);
        keskjärjestus(tipp.parem, list);
    }


    /**
     * leiab tipu, mida eemaldamisel tühja kohta viia
     * @param juur ette antud puu
     * @return tagastab elemendi, mis üles viia
     */
    private Tipp leiaJärgnev(Tipp juur) {
        Tipp praegune = juur;
        while (praegune.vasak != null) {
            praegune = praegune.vasak;
        }
        return praegune;
    }

    /**
     * tasakaalustab avl puu
     * @param juur ette antud puu juur
     * @return tagastab tasakaalustatud puu juure
     */
    private Tipp tasakaalustaPuu(Tipp juur) {
        if (juur == null) {
            return null;
        }
        //vasaku ja parema poole kõrguste vahe
        int vahe = kõrgusteVahe(juur);
        //kontrollime, millise pöörde peame sooritama ja sooritame
        if (vahe == 2) {
            if (kõrgusteVahe(juur.vasak) >= 0) {
                return paremPööre(juur);
            } else {
                juur.vasak = vasakPööre(juur.vasak);
                return paremPööre(juur);
            }
        }
        if (vahe == -2) {
            if (kõrgusteVahe(juur.parem) <= 0) {
                return vasakPööre(juur);
            } else {
                juur.parem = paremPööre(juur.parem);
                return vasakPööre(juur);
            }
        }

        return juur;
    }

    /**
     * leiame ette antud tipust kõrguse
     * @param tipp ette antud puu tipp
     * @return tagastame tipust leheni pikima ahela
     */
    private static int puuKõrgus(Tipp tipp) {
        if (tipp == null) {
            return 0;
        } else {
            return Math.max(puuKõrgus(tipp.vasak), puuKõrgus(tipp.parem)) + 1;
        }
    }

    /**
     * leiab kõrguste vahe
     * @param tipp ette antud tipp kust alates vahe leida
     * @return tagastab vahe
     */
    private int kõrgusteVahe(Tipp tipp) {
        return puuKõrgus(tipp.vasak) - puuKõrgus(tipp.parem);
    }

    /**
     * sooritab puus vasakpöörde
     * @param tipp tipp, kus pööre teha
     * @return tagastab tipu, mis liikus pöördega üles
     */
    public static Tipp vasakPööre(Tipp tipp) {
        Tipp y;

        y=tipp.parem;
        tipp.parem=y.vasak;
        y.vasak=tipp;

        return y;
    }

    /**
     * sooritab puus parempöörde
     * @param tipp ette antud tipp, kus pööre teha
     * @return tagastab tipu, mis liikus pöördega üles
     */
    public static Tipp paremPööre(Tipp tipp) {
        Tipp y;

        y=tipp.vasak;
        tipp.vasak=y.parem;
        y.parem=tipp;

        return y;
    }
}

